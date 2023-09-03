# crossbuilds

Install stuff

1 sbt "~fastLinkJS"
2 sbt "serverJVM/run"
3 npx tailwindcss -i ./style.css -o ./output.css; npm run dev
4 npm run dev

sbt "docker"


docker compose -f stack.yml up db
docker compose -f stack.yml  up -d
docker compose -f stack.yml down

docker volume rm crossbuilds_server