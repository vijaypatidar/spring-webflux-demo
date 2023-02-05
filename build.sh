./gradlew clean build
DOCKER_REPO_NAME=vijaypatidar31/webflux
VERSION_TAG=latest
docker build . -t "${DOCKER_REPO_NAME}:${VERSION_TAG}"
docker push "${DOCKER_REPO_NAME}:${VERSION_TAG}"