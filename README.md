# esteid-personal-data-file-reader
Java application to read personal data file from estonian ID-card (EstEID 2018)

## Running
```
./gradlew run
```

## Project creation
'''
podman run --rm -it -v "$PWD":/esteid-personal-data-file-reader -w /esteid-personal-data-file-reader docker.io/library/gradle:8.2-jdk11-alpine gradle init --type java-application --test-framework junit-jupiter
'''

## Resources
* https://www.id.ee/en/article/id-card-documentation-2/
* https://www.id.ee/wp-content/uploads/2020/02/ID1DeveloperGuide.pdf
* https://github.com/jnasmartcardio/jnasmartcardio
* https://github.com/martinpaljak/apdu4j
