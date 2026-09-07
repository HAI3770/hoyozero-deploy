import json, os, signal, subprocess, tempfile, time, urllib.request, threading
from urllib.parse import quote

BACKEND=os.getenv('BACKEND_URL','http://backend:8080'); TOKEN=os.getenv('HOYOZERO_RUNNER_TOKEN',''); NAME=os.getenv('RUNNER_NAME','docker-runner-1'); BUILD_TIMEOUT=int(os.getenv('BUILD_TIMEOUT_SECONDS','600'))
def post(path, data):
    req=urllib.request.Request(BACKEND+path, json.dumps(data).encode(), {'Content-Type':'application/json','X-Runner-Token':TOKEN})
    return json.loads(urllib.request.urlopen(req, timeout=30).read())
def run(cmd, cwd, log):
    p=subprocess.Popen(cmd,cwd=cwd,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True,start_new_session=True)
    timed_out=[False]
    def stop():
        timed_out[0]=True
        try: os.killpg(p.pid, signal.SIGTERM)
        except ProcessLookupError: pass
    timer=threading.Timer(BUILD_TIMEOUT, stop); timer.start()
    for line in p.stdout: log.append(line)
    p.wait(); timer.cancel()
    if timed_out[0]: raise RuntimeError('构建超时（超过%s秒）' % BUILD_TIMEOUT)
    if p.returncode: raise RuntimeError('命令失败(%s): %s' % (p.returncode,' '.join(cmd)))
def work(task):
    log=[]; root=tempfile.mkdtemp(prefix='hoyozero-%s-' % task['buildId'], dir='/workspace'); image=''; tag=''; commit=''; digest=''
    try:
        url=task['gitUrl']; user=task.get('gitUsername',''); tok=task.get('gitToken','')
        askpass = None
        askpass_dir = None
        if user and tok and url.startswith('https://'):
            askpass_dir = tempfile.mkdtemp(prefix='hoyozero-askpass-')
            askpass = os.path.join(askpass_dir, 'git-askpass')
            with open(askpass, 'w') as f:
                f.write('#!/bin/sh\ncase "$1" in\n*Username*) printf "%s" "$GIT_USER";;\n*) printf "%s" "$GIT_TOKEN";;\nesac\n')
            os.chmod(askpass, 0o700)
        env = os.environ.copy()
        if askpass:
            env.update({'GIT_ASKPASS': askpass, 'GIT_USERNAME': user, 'GIT_USER': user, 'GIT_TOKEN': tok, 'GIT_TERMINAL_PROMPT': '0'})
        p = subprocess.Popen(['git','clone','--depth','1','--branch',task['branch'],url,root], cwd=root, env=env, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        for line in p.stdout: log.append(line)
        p.wait()
        if p.returncode: raise RuntimeError('命令失败(%s): git clone' % p.returncode)
        commit=subprocess.check_output(['git','rev-parse','--short=12','HEAD'],cwd=root,text=True).strip()
        registry=task.get('registryUrl','').rstrip('/'); prefix=(registry+'/' if registry else '')+(task.get('registryNamespace','')+'/' if task.get('registryNamespace') else '')
        image=prefix+task['imageName']; tag='build-%s' % task['buildId']
        registry_user = task.get('registryUsername') or os.getenv('REGISTRY_USERNAME','')
        registry_token = task.get('registryToken') or os.getenv('REGISTRY_TOKEN','')
        if registry_user and registry_token:
            p=subprocess.Popen(['docker','login',registry,'--username',registry_user,'--password-stdin'],stdin=subprocess.PIPE,stdout=subprocess.PIPE,stderr=subprocess.STDOUT,text=True); out,_=p.communicate(registry_token+'\n'); log.append(out); p.wait()
            if p.returncode: raise RuntimeError('Registry 登录失败')
        run(['docker','buildx','build','--progress=plain','--platform',task.get('platform','linux/amd64'),'-f',task.get('dockerfilePath','Dockerfile'),'-t',image+':'+tag,'-t',image+':'+task.get('env','dev')+'-latest','--push',task.get('dockerContext','.')],root,log)
        try:
            digest=subprocess.check_output(['docker','buildx','imagetools','inspect',image+':'+tag,'--format','{{.Manifest.Digest}}'],text=True).strip()
        except subprocess.CalledProcessError:
            digest=''
            log.append('[Registry] 镜像已推送，但 HTTP Harbor 不支持 Digest 查询，继续完成构建\n')
        post('/internal/runner/result',{'buildId':task['buildId'],'status':'SUCCESS','log':''.join(log),'commit':commit,'image':image,'tag':tag,'digest':digest})
    except Exception as e:
        log.append('[错误] '+str(e)+'\n'); post('/internal/runner/result',{'buildId':task['buildId'],'status':'FAILED','log':''.join(log),'commit':commit,'image':image,'tag':tag,'reason':str(e)})
    finally:
        subprocess.run(['rm','-rf',root])
        if askpass_dir: subprocess.run(['rm','-rf',askpass_dir])
while True:
    try:
        task=post('/internal/runner/claim',{'runner':NAME})
        if task: work(task)
    except Exception as e: time.sleep(5)
