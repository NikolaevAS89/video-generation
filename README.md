# Video-generation
server for video generation service

## Startup
```
    docker-compose --env-file .env up -d 
```

### A Google service account secret file 
The Google service account secret file is required to communicate with Google Sheets.
A path to file is defined as environment variable in a .env file and is used in docker-compose.yml:
```
GOOGLE_SERVICE_ACCOUNT=service_account.json
```
Also, the file is copied during image build in a gsheets-poster-service module. 

## Shutdown
```
    docker-compose down
```

### Database
All used tables is described in [database.drawio](database.drawio).

### Modules
All modules and how is bound show in [videogeneration.drawio](videogeneration.drawio).

### REST API Examples
Examples is contained in [videogeneration.postman_collection.json](videogeneration.postman_collection.json) 
as a Postman (Utility to make requests) collection.