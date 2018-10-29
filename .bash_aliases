export WLP_HOME='/opt/wlp'
export WLP_SRV_NAME='default'
export APP_NAME='dma'
export USERNAME='fram'
export PASSWORD='fram'
export TABLE_NAME=''
alias stwlp='$WLP_HOME/bin/server start $WLP_SRV_NAME'
alias spwlp='$WLP_HOME/bin/server stop $WLP_SRV_NAME'
alias rmwlplogs='rm -rf $WLP_HOME/usr/servers/$WLP_SRV_NAME/logs/*'
alias rstwlp='spwlp && rmwlplogs  && stwlp'
alias cnfwlp='/usr/bin/vi $WLP_HOME/usr/servers/$WLP_SRV_NAME/server.xml'
alias monwlp='tail -f $WLP_HOME/usr/servers/$WLP_SRV_NAME/logs/messages.log'
alias logwlp='/usr/bin/vi $WLP_HOME/usr/servers/$WLP_SRV_NAME/logs/messages.log'
alias statwlp='$WLP_HOME/bin/server status $WLP_SRV_NAME'
alias pidwlp="ps -eaf | grep -v grep | grep $WLP_SRV_NAME'$' | awk '{print \$2}'"
alias kwlp='kill -9 $(pidwlp)'
alias appwlp='/usr/bin/vi $WLP_HOME/usr/servers/$WLP_SRV_NAME/apps/$APP_NAME.war.xml'
alias ldma='rm -f cookies.txt && curl -i --cookie cookies.txt --cookie-jar cookies.txt -X POST -d "j_username=$USERNAME" -d "j_password=$PASSWORD" "http://localhost/dma/j_security_check"'
alias guidma='curl --cookie cookies.txt --cookie-jar cookies.txt -X POST "http://localhost/dma/GetUserInfos" | jq .'
alias grdma='curl --cookie cookies.txt --cookie-jar cookies.txt -X POST "http://localhost/dma/GetResources" | jq .'
alias lodma='curl -i --cookie cookies.txt --cookie-jar cookies.txt -X POST "http://localhost/dma/Logout"'

JAVA_HOME=/opt/JDK/jre

JAVA_PATH=/opt/JDK/bin

export JAVA_HOME PATH

