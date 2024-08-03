# Atipera GitHub Repository Scanner

## Description
Spring Boot application that uses GitHub API to list user repositories, their branches and last commit sha.

## How to run
1. Clone the repository
2. Run the application
   1. If you have a GitHub token, you can add it to the `docker-compose.yml` file
   2. Run ```docker compose up``` in the root directory
3. Access the application at http://localhost:8080

## Example usage
#### Send a GET request to http://localhost:8080/api/v1/{username}/repositories
   - Replace `{username}` with the desired GitHub username

Example request: http://localhost:8080/api/v1/kubabartecki/repositories

Example response:
```json
[
  {
    "name": "atipera",
    "ownerLogin": "kubabartecki",
    "branches": [
      {
        "name": "master",
        "lastCommitSha": "4ab85f769f63bae889890b596418a8b13631c91d"
      }
    ]
  },
  {
    "name": "YourSkateTricks",
    "ownerLogin": "kubabartecki",
    "branches": [
      {
        "name": "kubernetes_deploy",
        "lastCommitSha": "e25727cc236c01391b470927505a186ddf203826"
      },
      {
        "name": "master",
        "lastCommitSha": "35c5bb2549bae3bbcad225896a3396700090a681"
      },
      {
        "name": "spring_security",
        "lastCommitSha": "7ab85f769f63bae889890b596418a8b13631c91d"
      }
    ]
  }
]
```
## Technologies
- Java 21
- Spring Boot 3.3.2
- Lombok
- WebFlux
- Docker
- GitHub API v3 https://docs.github.com/en/rest?apiVersion=2022-11-28
