PGPORT ?= 5441
PGHOST ?= localhost
PGUSER ?= postgres
PGDATABASE ?= hackathon
PGPASSWORD ?= jenkins

STORAGEPORT ?= 8990

.EXPORT_ALL_VARIABLES:

#Repl
ui-repl:
	cd client && rm -rf .cpcache/ && clj -A:dev:nrepl

back-repl:
	cd server && rm -rf .cpcache/ && clj -A:nrepl

#Build
ui-build:
	cd client && clj -A:prod



build-cambada:
	cd server && clj -A:build && mv target/app-1.0.0-SNAPSHOT-standalone.jar app.jar

#Run
back-run:
	cd server && clj -m app.rest

db-up:
	docker-compose up -d
db-down:
	docker-compose down
