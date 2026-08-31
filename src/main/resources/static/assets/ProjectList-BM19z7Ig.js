import{d as Y,c as P,o as u,a as y,u as Be,r as p,b as ne,l as H,m as Ye,e as l,w as t,h as s,B as D,C as re,f as _,g as Fe,i as Ke,D as m,j as i,E as We,G as ie,F as X,x as Q,q as f,t as T,H as ue,v as b,y as G,I as de,J as qe,K as I,L as Z,M as He,O as Xe,P as Qe,Q as Ze,s as B}from"./index-BYoypIih.js";import{g as el,a as ll,b as tl,d as al,u as ol,c as sl,e as nl}from"./project-Df025scy.js";import{g as rl}from"./server-DocE3uMh.js";import{t as il,d as ul,r as dl}from"./build-OyQCBafE.js";import{g as pl,a as cl}from"./user-DMujnzZ7.js";import{_ as vl}from"./_plugin-vue_export-helper-CwxF_alg.js";import{S as ml}from"./SearchSharp-DdjPnaaP.js";import{A as pe,C as fl}from"./CreateSharp-Du9n7hey.js";import{P as yl}from"./PeopleSharp-Hxl9rq0H.js";import{T as gl}from"./TrashSharp-DPIMAZjV.js";const hl={xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink",viewBox:"0 0 512 512"},_l=y("path",{d:"M150.38 253.68l21.94-23.3l11.65 11c73.63 69.36 147.51 111.56 234.45 133.07c11.73-32 12.77-67.22 2.64-101.58c-13.44-45.59-44.74-85.31-90.49-114.86c-40.25-26-76.6-32.09-115.09-38.54c-21.12-3.54-43-7.2-66.85-14.43c-43.78-13.28-89.69-52.74-90.15-53.13L33.4 30.15L32 63.33c-.1 2.56-2.42 63.57 14.22 147.77c17.58 89 50.24 155.85 97.07 198.63c38 34.69 87.62 53.9 136.93 53.9a185.88 185.88 0 0 0 27.78-2.07c41.72-6.32 76.43-27.27 96-57.75c-89.5-23.28-165.95-67.55-242-139.16z",fill:"currentColor"},null,-1),bl=y("path",{d:"M467.43 384.19c-16.83-2.59-33.13-5.84-49-9.77a158.49 158.49 0 0 1-12.13 25.68c-.74 1.25-1.51 2.49-2.29 3.71a583.43 583.43 0 0 0 58.55 12l15.82 2.44l4.86-31.63z",fill:"currentColor"},null,-1),Dl=[_l,bl],Pl=Y({name:"LeafSharp",render:function(R,c){return u(),P("svg",hl,Dl)}}),wl={xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink",viewBox:"0 0 512 512"},Tl=y("path",{d:"M256 144.03l-55.49-96.11h-79.43L256 281.61L390.92 47.92h-79.43L256 144.03z",fill:"currentColor"},null,-1),$l=y("path",{d:"M409.4 47.92L256 313.61L102.6 47.92H15.74L256 464.08L496.26 47.92H409.4z",fill:"currentColor"},null,-1),El=[Tl,$l],Il=Y({name:"LogoVue",render:function(R,c){return u(),P("svg",wl,El)}}),Rl={xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink",viewBox:"0 0 512 512"},kl=y("path",{d:"M256 48C141.31 48 48 141.31 48 256s93.31 208 208 208s208-93.31 208-208S370.69 48 256 48zm-56 296V168l144 88z",fill:"currentColor"},null,-1),Al=[kl],Cl=Y({name:"PlayCircleSharp",render:function(R,c){return u(),P("svg",Rl,Al)}}),xl={xmlns:"http://www.w3.org/2000/svg","xmlns:xlink":"http://www.w3.org/1999/xlink",viewBox:"0 0 512 512"},Ll=y("path",{d:"M256 48C141.13 48 48 141.13 48 256c0 114.69 93.32 208 208 208c114.86 0 208-93.14 208-208c0-114.69-93.31-208-208-208zm108 240H244a4 4 0 0 1-4-4V116a4 4 0 0 1 4-4h24a4 4 0 0 1 4 4v140h92a4 4 0 0 1 4 4v24a4 4 0 0 1-4 4z",fill:"currentColor"},null,-1),zl=[Ll],Ol=Y({name:"TimeSharp",render:function(R,c){return u(),P("svg",xl,zl)}}),Ul={class:"project-list"},Sl={class:"toolbar"},Ml={style:{display:"flex","align-items":"center","justify-content":"space-between"}},Nl={style:{display:"flex","align-items":"center",gap:"8px"}},jl={style:{"font-size":"16px","font-weight":"600"}},Jl={class:"project-info-item"},Vl={class:"project-info-item"},Gl={class:"project-info-item"},Bl={class:"project-info-item"},Yl={key:0,class:"project-info-item"},Fl={key:2,style:{"margin-top":"20px",display:"flex","justify-content":"flex-end"}},Kl={__name:"ProjectList",setup(U){const R=Fe(),c=Be(),S=p(!1),x=p(!1),L=p(!1),z=p(!1),M=p(null),O=p(null),N=p(""),ee=p(),j=p([]),ce=p([]),k=p([]),le=p([]),$=p([]),F=p([]),J=p(!1),E=p(null),A=p("all"),te=p([]),g=ne({page:1,pageSize:9,itemCount:0,showSizePicker:!0,pageSizes:[9,18,27]}),ve=[{label:"Java",value:"JAVA"},{label:"Vue",value:"VUE"}],K=p([{label:"mvn clean package -DskipTests",value:"mvn clean package -DskipTests"},{label:"mvn clean package",value:"mvn clean package"},{label:"mvn clean install -DskipTests",value:"mvn clean install -DskipTests"},{label:"gradle build",value:"gradle build"},{label:"gradle build -x test",value:"gradle build -x test"}]),me=[{label:"target/*.jar",value:"target/*.jar"},{label:"target/*.war",value:"target/*.war"},{label:"build/libs/*.jar",value:"build/libs/*.jar"},{label:"dist",value:"dist"},{label:"build",value:"build"},{label:"out",value:"out"}],ae=p([]),fe=p([{label:"开发环境",value:"development"},{label:"测试环境",value:"test"},{label:"生产环境",value:"production"}]);H(()=>k.value.includes("ADMIN"));const W=H(()=>k.value.includes("ADMIN")||k.value.includes("PROJECT_ADMIN")),ye=H(()=>W.value),n=ne({name:"",description:"",gitUrl:"",branch:"master",gitUsername:"",gitPassword:"",projectType:"JAVA",buildCommand:"",buildDir:"",serverIds:[],autoDeploy:0,deployScript:"",deployPath:"/home/deploy/",appPort:8080,env:"development"}),ge={name:{required:!0,message:"请输入项目名称",trigger:"blur"},gitUrl:{required:!0,message:"请输入Git地址",trigger:"blur"},projectType:{required:!0,message:"请选择项目类型",trigger:"change"},buildCommand:{required:!0,message:"请输入构建命令",trigger:"blur"},buildDir:{required:!0,message:"请输入产物路径",trigger:"blur"}},w=async()=>{try{S.value=!0;const o={current:g.page,size:g.pageSize};N.value&&(o.name=N.value),A.value&&A.value!=="all"&&(o.env=A.value);const e=await el(o);j.value=e.records,g.itemCount=e.total}catch(o){console.error(o)}finally{S.value=!1}},he=async()=>{try{const o=await rl({current:1,size:100});ce.value=o.records,ae.value=o.records.map(e=>({label:e.name,value:e.id}))}catch(o){console.error(o)}},_e=o=>{g.page=o,w()},be=o=>{g.pageSize=o,g.page=1,w()},oe=async o=>{if(o){O.value=o.id;try{const e=await ll(o.id),r=e.project,d=e.serverIds||[];Object.assign(n,{name:r.name||"",description:r.description||"",gitUrl:r.gitUrl||"",branch:r.branch||"master",gitUsername:r.gitUsername||"",gitPassword:r.gitPassword||"",projectType:r.projectType||"JAVA",buildCommand:r.buildCommand||"",buildDir:r.buildDir||"",serverIds:d,autoDeploy:r.autoDeploy!=null?r.autoDeploy:0,deployScript:r.deployScript||V(r.projectType),deployPath:r.deployPath||"/home/deploy/",appPort:r.appPort||8080,env:r.env||"development"})}catch(e){console.error("获取项目详情失败:",e),c.error("获取项目详情失败")}}else O.value=null,Object.assign(n,{name:"",description:"",gitUrl:"",branch:"master",gitUsername:"",gitPassword:"",projectType:"JAVA",buildCommand:"",buildDir:"",serverIds:[],autoDeploy:0,deployScript:V("JAVA"),deployPath:"/home/deploy/",appPort:8080,env:"development"});x.value=!0},De=async()=>{var o;try{await((o=ee.value)==null?void 0:o.validate());const e={...n,serverIds:n.serverIds||[],autoDeploy:n.autoDeploy||0,deployPath:n.deployPath||"/home/deploy/",appPort:n.appPort||8080};O.value?(e.id=O.value,await ol(e),c.success("更新成功")):(await sl(e),c.success("添加成功")),x.value=!1,w()}catch(e){console.error(e)}},Pe=async o=>{try{await al(o),c.success("删除成功"),w()}catch(e){console.error(e)}},we=o=>{o==="JAVA"?(K.value=[{label:"mvn clean package -DskipTests",value:"mvn clean package -DskipTests"},{label:"mvn clean package",value:"mvn clean package"},{label:"mvn clean install -DskipTests",value:"mvn clean install -DskipTests"},{label:"mvn clean install",value:"mvn clean install"},{label:"gradle build -x test",value:"gradle build -x test"},{label:"gradle build",value:"gradle build"},{label:"gradle bootJar",value:"gradle bootJar"}],n.buildCommand||(n.buildCommand="mvn clean package -DskipTests")):o==="VUE"&&(K.value=[{label:"npm install && npm run build",value:"npm install && npm run build"},{label:"npm run build",value:"npm run build"},{label:"yarn install && yarn build",value:"yarn install && yarn build"},{label:"yarn build",value:"yarn build"},{label:"pnpm install && pnpm build",value:"pnpm install && pnpm build"},{label:"pnpm build",value:"pnpm build"}],n.buildCommand||(n.buildCommand="npm install && npm run build")),n.autoDeploy===1&&(n.deployScript=V(o))},Te=o=>{o===1&&(n.deployScript=V(n.projectType))},V=o=>(o||n.projectType)==="VUE"?`#!/bin/bash
# Vue 项目部署脚本

DEPLOY_DIR="{{uploadPath}}"
WEB_ROOT="/www/wwwroot"
PROJECT_NAME="{{projectName}}"
TARGET_DIR="$WEB_ROOT/$PROJECT_NAME"
BACKUP_DIR="$WEB_ROOT/backup"

echo "========================================"
echo "开始部署Vue项目"
echo "========================================"

# 1. 备份旧版本
echo "[步骤1] 备份旧版本..."
if [ -d "$TARGET_DIR" ]; then
  mkdir -p $BACKUP_DIR
  BACKUP_NAME="\${PROJECT_NAME}_$(date +%Y%m%d_%H%M%S)"
  mv $TARGET_DIR $BACKUP_DIR/$BACKUP_NAME
  echo "已备份为: $BACKUP_NAME"

  # 保留最近3个备份
  ls -t $BACKUP_DIR | grep "^\${PROJECT_NAME}_" | tail -n +4 | xargs -I {} rm -rf $BACKUP_DIR/{}
fi

# 2. 部署新版本
echo "[步骤2] 部署新版本..."
mkdir -p $TARGET_DIR

# 检查是否为压缩包
if [ -f "$DEPLOY_DIR/dist.zip" ]; then
  echo "检测到 dist.zip 文件"
  # 先解压到临时目录
  cd $DEPLOY_DIR
  unzip -o -q dist.zip

  # 检查解压后的结构
  if [ -d "$DEPLOY_DIR/dist" ]; then
    # 解压后有 dist 目录，复制其内容
    cp -r $DEPLOY_DIR/dist/* $TARGET_DIR/
    echo "已解压 dist.zip 并复制到 $TARGET_DIR"
  else
    # 解压后直接是文件，移动到目标目录
    mv $DEPLOY_DIR/* $TARGET_DIR/ 2>/dev/null || true
    echo "已解压 dist.zip 到 $TARGET_DIR"
  fi
elif [ -f "$DEPLOY_DIR/dist.tar.gz" ]; then
  tar -xzf $DEPLOY_DIR/dist.tar.gz -C $DEPLOY_DIR
  cp -r $DEPLOY_DIR/dist/* $TARGET_DIR/
  echo "已解压 dist.tar.gz 到 $TARGET_DIR"
elif [ -d "$DEPLOY_DIR/dist" ]; then
  cp -r $DEPLOY_DIR/dist/* $TARGET_DIR/
  echo "已复制 dist 目录到 $TARGET_DIR"
else
  echo "错误: 未找到构建产物（dist目录或压缩包）"
  exit 1
fi

# 3. 设置文件权限
echo "[步骤3] 设置文件权限..."
chmod -R 755 $TARGET_DIR

echo "========================================"
echo "部署完成！"
echo "部署目录: $TARGET_DIR"
echo "访问路径: http://your-domain/$PROJECT_NAME"
echo "========================================"`:`#!/bin/bash
# Java 项目部署脚本

APP_PORT={{appPort}}
DEPLOY_DIR="{{uploadPath}}"
LOG_FILE="$DEPLOY_DIR/app.log"

echo "========================================"
echo "开始部署Java应用"
echo "========================================"

# 1. 停止旧进程
echo "[步骤1] 正在停止旧进程..."
PID=$(lsof -t -i:$APP_PORT 2>/dev/null)
if [ ! -z "$PID" ]; then
  kill -15 $PID
  sleep 3
  if ps -p $PID > /dev/null 2>&1; then
    kill -9 $PID
    echo "已强制停止进程: $PID"
  else
    echo "已优雅停止进程: $PID"
  fi
else
  echo "未找到运行中的进程"
fi

sleep 2

# 2. 查找新上传的jar（备份操作已在上传前完成）
echo "[步骤2] 查找jar文件..."
JAR_FILE=$(ls -t $DEPLOY_DIR/*.jar 2>/dev/null | grep -v '\\.bak$' | head -n 1)

if [ -z "$JAR_FILE" ] || [ ! -f "$JAR_FILE" ]; then
  echo "错误: 未找到可用的jar文件"
  exit 1
fi

JAR_NAME=$(basename "$JAR_FILE")
echo "找到jar文件: $JAR_NAME"

# 3. 启动新应用
echo "[步骤3] 正在启动应用..."
cd $DEPLOY_DIR

# 清空旧日志
> $LOG_FILE

# 后台启动应用
nohup java -jar -Xms512m -Xmx1024m -Dserver.port=$APP_PORT "$JAR_FILE" > $LOG_FILE 2>&1 &
NEW_PID=$!

echo "应用已启动，PID: $NEW_PID"
echo "JAR文件: $JAR_NAME"
echo "日志文件: $LOG_FILE"

# 4. 等待应用启动并实时显示日志
echo "[步骤4] 等待应用启动..."
echo "----------------------------------------"
echo "应用启动日志："
echo "----------------------------------------"

# 实时显示日志并检测启动成功
START_TIME=$(date +%s)
TIMEOUT=60
STARTED=false

# 使用 tail -f 实时显示日志，同时检测启动状态
(
  tail -f $LOG_FILE &
  TAIL_PID=$!

  while true; do
    CURRENT_TIME=$(date +%s)
    ELAPSED=$((CURRENT_TIME - START_TIME))

    # 检查超时
    if [ $ELAPSED -gt $TIMEOUT ]; then
      kill $TAIL_PID 2>/dev/null
      echo ""
      echo "----------------------------------------"
      echo "警告: 应用启动超时（60秒）"
      echo "请检查日志文件: $LOG_FILE"
      exit 1
    fi

    # 检查端口是否已监听
    if lsof -t -i:$APP_PORT > /dev/null 2>&1; then
      sleep 2
      kill $TAIL_PID 2>/dev/null
      STARTED=true
      break
    fi

    # 检查进程是否还在运行
    if ! ps -p $NEW_PID > /dev/null 2>&1; then
      kill $TAIL_PID 2>/dev/null
      echo ""
      echo "----------------------------------------"
      echo "错误: 应用进程已退出"
      echo "请检查日志文件: $LOG_FILE"
      exit 1
    fi

    sleep 1
  done

  if [ "$STARTED" = true ]; then
    echo ""
    echo "----------------------------------------"
    echo "应用启动成功！"
    echo "========================================"
    echo "部署完成！"
    echo "JAR文件: $JAR_NAME"
    echo "应用端口: $APP_PORT"
    echo "应用PID: $NEW_PID"
    echo "日志文件: $LOG_FILE"
    echo "========================================"
    exit 0
  fi
) || exit 1`,$e=async o=>{try{const e=await il(o);c.success("构建任务已创建，正在跳转..."),R.push(`/build-detail/${e.buildId}`)}catch(e){console.error(e),c.error("部署失败")}},Ee=async()=>{try{const o=await pl();k.value=o.permissions||[]}catch(o){console.error("加载用户权限失败",o)}},Ie=[{title:"用户",key:"userId",render:(o,e)=>{const r=le.value.map(d=>({label:`${d.nickname||d.username} (${d.username})`,value:d.id}));return B(I,{value:o.userId,options:r,placeholder:"请选择用户",style:{width:"250px"},onUpdateValue:d=>{$.value[e].userId=d}})}},{title:"角色",key:"roleType",render:(o,e)=>{const r=[{label:"开发者",value:"DEVELOPER"},{label:"成员",value:"MEMBER"}];return B(I,{value:o.roleType,options:r,style:{width:"120px"},onUpdateValue:d=>{$.value[e].roleType=d}})}},{title:"操作",key:"actions",width:100,render:(o,e)=>o.roleType==="OWNER"?B(G,{type:"success"},{default:()=>"拥有者"}):B(m,{text:!0,type:"error",size:"small",onClick:()=>Ae(e)},{default:()=>"移除"})}],Re=async o=>{M.value=o;try{const e=await cl({current:1,size:100});le.value=e.records;const r=await tl(o);$.value=r.map(d=>({userId:d.userId,roleType:d.roleType,user:d.user})),L.value=!0}catch{c.error("加载成员列表失败")}},ke=()=>{$.value.push({userId:null,roleType:"MEMBER"})},Ae=o=>{$.value.splice(o,1)},Ce=async()=>{try{const o=$.value.filter(e=>e.userId&&e.roleType!=="OWNER").map(e=>({userId:e.userId,roleType:e.roleType}));await nl(M.value,o),c.success("保存成功"),L.value=!1}catch{c.error("保存失败")}},xe=async o=>{M.value=o,E.value=null,z.value=!0;try{J.value=!0;const e=await ul(o);F.value=e||[]}catch(e){c.error("加载备份列表失败"),console.error(e)}finally{J.value=!1}},Le=async()=>{if(!E.value){c.error("请选择要回退的版本");return}try{const o=await dl({projectId:M.value,backupFileName:E.value});c.success("回退任务已创建，正在跳转..."),z.value=!1,R.push(`/build-detail/${o.buildId}`)}catch(o){console.error(o),c.error("回退失败")}},ze=async()=>{try{const e=await(await fetch("/api/project/envs",{headers:{Authorization:`${localStorage.getItem("token")}`}})).json();e.code===200&&(te.value=e.data||[])}catch(o){console.error("加载环境列表失败:",o)}},Oe=o=>{A.value=o,g.page=1,w()},se=o=>({development:"开发环境",test:"测试环境",production:"生产环境"})[o]||o,Ue=o=>({development:"info",test:"warning",production:"error"})[o]||"default";return Ye(()=>{Ee(),w(),he(),ze()}),(o,e)=>{const r=_("n-icon"),d=_("n-input"),h=_("n-text"),Se=_("n-card"),Me=_("n-gi"),Ne=_("n-grid"),v=_("n-form-item"),je=_("n-radio-group"),Je=_("n-input-number"),Ve=_("n-form"),q=_("n-modal"),Ge=_("n-data-table");return u(),P("div",Ul,[y("div",Sl,[l(s(D),{vertical:"",size:16},{default:t(()=>[l(s(D),null,{default:t(()=>[l(d,{value:N.value,"onUpdate:value":e[0]||(e[0]=a=>N.value=a),placeholder:"搜索项目名称",clearable:"",style:{width:"250px"},onKeyup:Ke(w,["enter"])},{suffix:t(()=>[l(s(m),{text:"",onClick:w},{default:t(()=>[l(r,null,{default:t(()=>[l(s(ml))]),_:1})]),_:1})]),_:1},8,["value"]),l(s(m),{type:"primary",onClick:e[1]||(e[1]=a=>oe())},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(pe))]),_:1})]),default:t(()=>[e[26]||(e[26]=i(" 新增项目 ",-1))]),_:1})]),_:1}),l(s(We),{value:A.value,"onUpdate:value":[e[2]||(e[2]=a=>A.value=a),Oe],type:"line"},{default:t(()=>[l(s(ie),{name:"all",tab:"全部"}),(u(!0),P(X,null,Q(te.value,a=>(u(),f(s(ie),{key:a,name:a,tab:se(a)},null,8,["name","tab"]))),128))]),_:1},8,["value"])]),_:1})]),l(s(re),{show:S.value},{default:t(()=>[!S.value&&j.value.length===0?(u(),f(s(ue),{key:0,description:"暂无项目数据",style:{"margin-top":"60px"}})):(u(),f(Ne,{key:1,"x-gap":16,"y-gap":16,cols:3,responsive:"screen"},{default:t(()=>[(u(!0),P(X,null,Q(j.value,a=>(u(),f(Me,{key:a.id},{default:t(()=>[l(Se,{bordered:!0,hoverable:"",class:"project-card"},{header:t(()=>[y("div",Ml,[y("div",Nl,[l(r,{size:"24",color:a.projectType==="JAVA"?"#6db33f":"#18a058"},{default:t(()=>[a.projectType==="JAVA"?(u(),f(s(Pl),{key:0})):(u(),f(s(Il),{key:1}))]),_:2},1032,["color"]),y("div",null,[y("div",jl,b(a.name),1),l(h,{depth:"3",style:{"font-size":"12px"}},{default:t(()=>[i("ID: "+b(a.id)+" ",1)]),_:2},1024),l(h,{depth:"3",style:{"font-size":"12px"}},{default:t(()=>[i(b(a.description),1)]),_:2},1024)])]),l(s(G),{type:a.projectType==="JAVA"?"info":"success",size:"small"},{default:t(()=>[i(b(a.projectType),1)]),_:2},1032,["type"])])]),footer:t(()=>[l(s(D),{justify:"space-between"},{default:t(()=>[l(s(D),{size:8},{default:t(()=>[W.value||k.value.includes("DEVELOPER")?(u(),f(s(de),{key:0,onPositiveClick:C=>$e(a.id)},{trigger:t(()=>[l(s(m),{secondary:"",size:"small",type:"primary"},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(Cl))]),_:1})]),default:t(()=>[e[32]||(e[32]=i(" 立即构建 ",-1))]),_:1})]),default:t(()=>[e[33]||(e[33]=i(" 确定要部署该项目吗？ ",-1))]),_:1},8,["onPositiveClick"])):T("",!0),(W.value||k.value.includes("DEVELOPER"))&&a.autoDeploy===1&&a.projectType==="JAVA"?(u(),f(s(m),{key:1,secondary:"",size:"small",type:"warning",onClick:C=>xe(a.id)},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(Ol))]),_:1})]),default:t(()=>[e[34]||(e[34]=i(" 回退 ",-1))]),_:1},8,["onClick"])):T("",!0)]),_:2},1024),ye.value?(u(),f(s(D),{key:0,size:6},{default:t(()=>[l(s(m),{secondary:"",size:"small",type:"warning",onClick:C=>Re(a.id)},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(yl))]),_:1})]),_:1},8,["onClick"]),l(s(m),{secondary:"",size:"small",type:"success",onClick:C=>oe(a)},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(fl))]),_:1})]),_:1},8,["onClick"]),l(s(de),{onPositiveClick:C=>Pe(a.id)},{trigger:t(()=>[l(s(m),{secondary:"",size:"small",type:"error"},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(gl))]),_:1})]),_:1})]),default:t(()=>[e[35]||(e[35]=i(" 确定要删除该项目吗？ ",-1))]),_:1},8,["onPositiveClick"])]),_:2},1024)):T("",!0)]),_:2},1024)]),default:t(()=>[l(s(D),{vertical:"",size:12},{default:t(()=>[y("div",Jl,[l(h,{depth:"3"},{default:t(()=>[...e[27]||(e[27]=[i("Git地址：",-1)])]),_:1}),l(h,null,{default:t(()=>[i(b(a.gitUrl),1)]),_:2},1024)]),y("div",Vl,[l(h,{depth:"3"},{default:t(()=>[...e[28]||(e[28]=[i("分支：",-1)])]),_:1}),l(s(G),{size:"small",type:"warning"},{default:t(()=>[i(b(a.branch),1)]),_:2},1024)]),y("div",Gl,[l(h,{depth:"3"},{default:t(()=>[...e[29]||(e[29]=[i("环境：",-1)])]),_:1}),l(s(G),{size:"small",type:Ue(a.env)},{default:t(()=>[i(b(se(a.env||"development")),1)]),_:2},1032,["type"])]),y("div",Bl,[l(h,{depth:"3"},{default:t(()=>[...e[30]||(e[30]=[i("创建时间：",-1)])]),_:1}),l(h,null,{default:t(()=>[i(b(a.createTime),1)]),_:2},1024)]),a.lastDeployTime?(u(),P("div",Yl,[l(h,{depth:"3"},{default:t(()=>[...e[31]||(e[31]=[i("最近部署：",-1)])]),_:1}),l(h,{type:"success"},{default:t(()=>[i(b(a.lastDeployTime),1)]),_:2},1024)])):T("",!0)]),_:2},1024)]),_:2},1024)]),_:2},1024))),128))]),_:1})),j.value.length>0?(u(),P("div",Fl,[l(s(qe),{page:g.page,"onUpdate:page":[e[3]||(e[3]=a=>g.page=a),_e],"page-size":g.pageSize,"onUpdate:pageSize":[e[4]||(e[4]=a=>g.pageSize=a),be],"page-count":Math.ceil(g.itemCount/g.pageSize),"page-sizes":g.pageSizes,"show-size-picker":""},null,8,["page","page-size","page-count","page-sizes"])])):T("",!0)]),_:1},8,["show"]),l(q,{show:x.value,"onUpdate:show":e[21]||(e[21]=a=>x.value=a),title:O.value?"编辑项目":"新增项目",preset:"dialog",style:{width:"800px"},"show-icon":!1},{action:t(()=>[l(s(D),null,{default:t(()=>[l(s(m),{onClick:e[20]||(e[20]=a=>x.value=!1)},{default:t(()=>[...e[39]||(e[39]=[i("取消",-1)])]),_:1}),l(s(m),{type:"primary",onClick:De},{default:t(()=>[...e[40]||(e[40]=[i("确定",-1)])]),_:1})]),_:1})]),default:t(()=>[l(Ve,{ref_key:"formRef",ref:ee,model:n,rules:ge,"label-placement":"left","label-width":"110"},{default:t(()=>[l(v,{label:"项目名称",path:"name"},{default:t(()=>[l(d,{value:n.name,"onUpdate:value":e[5]||(e[5]=a=>n.name=a),placeholder:"请输入项目名称"},null,8,["value"])]),_:1}),l(v,{label:"项目描述",path:"description"},{default:t(()=>[l(d,{value:n.description,"onUpdate:value":e[6]||(e[6]=a=>n.description=a),type:"textarea",rows:3,placeholder:"请输入项目描述"},null,8,["value"])]),_:1}),l(v,{label:"Git地址",path:"gitUrl"},{default:t(()=>[l(d,{value:n.gitUrl,"onUpdate:value":e[7]||(e[7]=a=>n.gitUrl=a),placeholder:"https://github.com/xxx/xxx.git"},null,8,["value"])]),_:1}),l(v,{label:"分支",path:"branch"},{default:t(()=>[l(d,{value:n.branch,"onUpdate:value":e[8]||(e[8]=a=>n.branch=a),placeholder:"master"},null,8,["value"])]),_:1}),l(v,{label:"Git用户名",path:"gitUsername"},{default:t(()=>[l(d,{value:n.gitUsername,"onUpdate:value":e[9]||(e[9]=a=>n.gitUsername=a),placeholder:"请输入Git用户名（可选）"},null,8,["value"])]),_:1}),l(v,{label:"Git密码",path:"gitPassword"},{default:t(()=>[l(d,{value:n.gitPassword,"onUpdate:value":e[10]||(e[10]=a=>n.gitPassword=a),type:"password","show-password-on":"click",placeholder:"请输入Git密码（可选）"},null,8,["value"])]),_:1}),l(v,{label:"项目类型",path:"projectType"},{default:t(()=>[l(s(I),{value:n.projectType,"onUpdate:value":[e[11]||(e[11]=a=>n.projectType=a),we],options:ve,placeholder:"请选择项目类型"},null,8,["value"])]),_:1}),l(v,{label:"环境",path:"env"},{default:t(()=>[l(s(I),{value:n.env,"onUpdate:value":e[12]||(e[12]=a=>n.env=a),options:fe.value,tag:"",filterable:"",placeholder:"请选择或自定义环境"},null,8,["value","options"])]),_:1}),l(v,{label:"构建命令",path:"buildCommand"},{default:t(()=>[l(s(I),{value:n.buildCommand,"onUpdate:value":e[13]||(e[13]=a=>n.buildCommand=a),options:K.value,tag:"",filterable:"",placeholder:"请选择或自定义构建命令"},null,8,["value","options"])]),_:1}),l(v,{label:"产物路径",path:"buildDir"},{default:t(()=>[l(s(I),{value:n.buildDir,"onUpdate:value":e[14]||(e[14]=a=>n.buildDir=a),options:me,tag:"",filterable:"",placeholder:"请选择或自定义产物路径"},null,8,["value"])]),_:1}),l(v,{label:"部署服务器",path:"serverIds"},{default:t(()=>[l(s(I),{value:n.serverIds,"onUpdate:value":e[15]||(e[15]=a=>n.serverIds=a),options:ae.value,multiple:"",clearable:"",placeholder:"请选择部署服务器（可多选）"},null,8,["value","options"])]),_:1}),l(v,{label:"自动部署",path:"autoDeploy"},{default:t(()=>[l(je,{value:n.autoDeploy,"onUpdate:value":[e[16]||(e[16]=a=>n.autoDeploy=a),Te]},{default:t(()=>[l(s(Z),{value:1},{default:t(()=>[...e[36]||(e[36]=[i("是",-1)])]),_:1}),l(s(Z),{value:0},{default:t(()=>[...e[37]||(e[37]=[i("否",-1)])]),_:1})]),_:1},8,["value"])]),_:1}),n.autoDeploy===1?(u(),f(v,{key:0,label:"应用端口",path:"appPort"},{default:t(()=>[l(Je,{value:n.appPort,"onUpdate:value":e[17]||(e[17]=a=>n.appPort=a),min:1,max:65535,placeholder:"8080",style:{width:"100%"}},null,8,["value"])]),_:1})):T("",!0),n.autoDeploy===1?(u(),f(v,{key:1,label:"部署目录",path:"deployPath"},{feedback:t(()=>[l(h,{depth:"3",style:{"font-size":"12px"}},{default:t(()=>[...e[38]||(e[38]=[i("服务器上存放应用的目录，默认为 /home/deploy/",-1)])]),_:1})]),default:t(()=>[l(d,{value:n.deployPath,"onUpdate:value":e[18]||(e[18]=a=>n.deployPath=a),placeholder:"/home/deploy/"},null,8,["value"])]),_:1})):T("",!0),n.autoDeploy===1?(u(),f(v,{key:2,label:"部署脚本",path:"deployScript"},{default:t(()=>[l(d,{value:n.deployScript,"onUpdate:value":e[19]||(e[19]=a=>n.deployScript=a),type:"textarea",rows:8,placeholder:"部署脚本将自动生成，也可自定义"},null,8,["value"])]),_:1})):T("",!0)]),_:1},8,["model"])]),_:1},8,["show","title"]),l(q,{show:L.value,"onUpdate:show":e[23]||(e[23]=a=>L.value=a),title:"项目成员管理",preset:"dialog",style:{width:"700px"},"show-icon":!1},{action:t(()=>[l(s(D),null,{default:t(()=>[l(s(m),{onClick:e[22]||(e[22]=a=>L.value=!1)},{default:t(()=>[...e[42]||(e[42]=[i("取消",-1)])]),_:1}),l(s(m),{type:"primary",onClick:Ce},{default:t(()=>[...e[43]||(e[43]=[i("保存",-1)])]),_:1})]),_:1})]),default:t(()=>[l(s(D),{vertical:""},{default:t(()=>[l(s(m),{type:"primary",size:"small",onClick:ke},{icon:t(()=>[l(r,null,{default:t(()=>[l(s(pe))]),_:1})]),default:t(()=>[e[41]||(e[41]=i(" 添加成员 ",-1))]),_:1}),l(Ge,{columns:Ie,data:$.value,pagination:!1},null,8,["data"])]),_:1})]),_:1},8,["show"]),l(q,{show:z.value,"onUpdate:show":e[25]||(e[25]=a=>z.value=a),title:"选择要回退的版本",preset:"dialog",style:{width:"700px"},"show-icon":!1},{action:t(()=>[l(s(D),null,{default:t(()=>[l(s(m),{onClick:e[24]||(e[24]=a=>z.value=!1)},{default:t(()=>[...e[44]||(e[44]=[i("取消",-1)])]),_:1}),l(s(m),{type:"warning",disabled:!E.value,onClick:Le},{default:t(()=>[...e[45]||(e[45]=[i(" 确认回退 ",-1)])]),_:1},8,["disabled"])]),_:1})]),default:t(()=>[l(s(re),{show:J.value},{default:t(()=>[!J.value&&F.value.length===0?(u(),f(s(ue),{key:0,description:"暂无备份版本"})):(u(),f(s(He),{key:1,bordered:""},{default:t(()=>[(u(!0),P(X,null,Q(F.value,(a,C)=>(u(),f(s(Xe),{key:C,style:{cursor:"pointer"},class:Qe({"selected-backup":E.value===a.fileName}),onClick:Wl=>E.value=a.fileName},{prefix:t(()=>[l(s(Z),{checked:E.value===a.fileName},null,8,["checked"])]),default:t(()=>[l(s(Ze),null,{header:t(()=>[l(h,{strong:""},{default:t(()=>[i(b(a.fileName),1)]),_:2},1024)]),description:t(()=>[l(h,{depth:"3"},{default:t(()=>[i("备份时间: "+b(a.date),1)]),_:2},1024)]),_:2},1024)]),_:2},1032,["class","onClick"]))),128))]),_:1}))]),_:1},8,["show"])]),_:1},8,["show"])])}}},st=vl(Kl,[["__scopeId","data-v-835754c8"]]);export{st as default};
