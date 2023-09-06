# Rocfreestands

This repo contains the code used to develop [Rocfreestands](https://rocfreestands.com). The frontend is built using ScalaJS and the Tyrian framework. The backend is built using http4s, postgres, and the typelevel FP stack. The frontend and backend commuincate via rest endpoints defined using the Smithy IDL. The app is deployed to Digital ocean and netlify, through the help of docker and Github Actions 


## Environment setup

The following tools where used to set up the development environment, if these requirements are already met this step can be skipped \
- [nvm](https://github.com/nvm-sh/nvm) 
- [sdkman](https://sdkman.io/)
- [docker]()

```
# JS stuff
nvm install node 20
nvm use node 20
npm install

# Scala stuff
sdk install java 22.3.1.r19-grl
sdk install scala 3.3.0
sdk install sbt 1.9.0
```

## Starting The Backend

### Using Docker 
The fastest way to start the backend is to use Docker Compose. This will automatically start a postgres service, then the server as well as create volumes to persist data
Simply run 

`docker compose -f stack.yml  up -d`

To stop the services run 

`docker compose -f stack.yml down` 

To remove any volumes run 
```bash
docker volume ls
docker volume rm <names of volumes to remove>
```

To confirm that the services started correctly navigate to http://localhost:8081/docs. The swagger documentation should be present

To change ENV variables alter them in the  `stack.yml` file prior to the first time the you run `docker compose up`
### Using SBT
Even when the server is being run with SBT postgres is still run via Docker. To start just postgres run

`docker compose -f stack.yml -p dblocal up db`

Then run 

`sbt "serverJVM/run"`

To confirm that the services started correctly navigate to http://localhost:8081/docs. The swagger documentation should be present

## Starting The Frontend

First make sure the backend is running as the frontend will immediately try and pull data from the backend.

To compile the frontend from scala to JS simply run

`sbt "~fastLinkJS"` 

Note that the `~` enables live reloading of code changes.

To build the CSS styles defined using tailwind CSS, in a separate terminal run 

`npx tailwindcss -i ./style.css -o ./output.css`

Note that this step will need to be rerun anytime a tailwind style is added

Finally, in a third terminal run

`npm run dev`