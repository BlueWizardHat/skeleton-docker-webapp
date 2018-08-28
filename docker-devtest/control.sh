#!/usr/bin/env bash

devtestdir=$(readlink -f $(dirname "$0"))
projectdir=$(dirname "$devtestdir")
backenddir="$projectdir/backend"


function mvnbuild() {
	doTests="$1"

	if [ $doTests == "skip" ]; then
		skipTestsArg=" -DskipTests"
	else
		skipTestsArg=""
	fi
	mvncmd="mvn clean verify -P demo$skipTestsArg"

	echo -e "\n\e[1;33m* Building in:\e[0m \e[32m${backenddir}\e[0m"
	cd $backenddir
	echo -e "\e[1;33m* Command:    \e[0m \e[32m${mvncmd}\e[0m\n"
	$mvncmd
	maven_exit_code=$?
	if [ $maven_exit_code != 0 ]; then
		exit $maven_exit_code
	fi
}


while [[ $# -gt 0 ]]; do
	action="$1"
	shift

	case "$action" in
		build)
			mvnbuild yes
			;;
		skiptests)
			mvnbuild skip
			;;
		start|up)
			echo -e "\n\e[1;33m* Starting containers as daemons\e[0m\n"
			cd $devtestdir
			docker-compose up -d
			;;
		restart)
			echo -e "\n\e[1;33m* Restarting backend\e[0m\n"
			cd $devtestdir
			docker-compose kill backend && docker-compose start backend
			;;
		run)
			echo -e "\n\e[1;33m* Starting containers blocking\e[0m\n"
			cd $devtestdir
			docker-compose up
			;;
		logs)
			echo -e "\n\e[1;33m* Tailing logs\e[0m\n"
			cd $devtestdir
			docker-compose logs -f --tail=20
			;;
		javalogs|backendlogs)
			echo -e "\n\e[1;33m* Tailing logs for java projects only\e[0m\n"
			cd $devtestdir
			docker-compose logs -f --tail=20 backend
			;;
		stop|down)
			echo -e "\n\e[1;33m* Stopping containers\e[0m\n"
			cd $devtestdir
			docker-compose kill && docker-compose down
			;;
		*)
			echo "Unknown argument: $action"
			echo "Usage: $0 build|start|up|run|restart|javalogs|logs|stop|down"
			;;
	esac
done
