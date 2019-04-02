docker-ip() {
    docker inspect --format '{{ .NetworkSettings.IPAddress }}' "$@"
}
