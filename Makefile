#Repl
ui-repl:
	cd client && rm -rf .cpcache/ && clj -A:dev:nrepl

back-repl:
	cd server && rm -rf .cpcache/ && clj -A:nrepl


#Build
ui-build:
	cd client && clj -A:prod

#Run
back-run:
	cd server && clj -m app.rest
