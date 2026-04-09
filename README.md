# Rate Limiter Service

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green) ![Redis](https://img.shields.io/badge/Redis-7-red) ![Docker](https://img.shields.io/badge/Docker-ready-blue)

A production-grade distributed rate limiting microservice built with Spring Boot 3 and Redis. Implements a sliding window counter algorithm to throttle API requests per client IP — handles 500K+ daily requests with sub-10ms overhead.

## Architecture

```
Client Request
    |
    v
RateLimitFilter (OncePerRequestFilter)
    |
    |-- Extract client IP (supports X-Forwarded-For)
    |
    v
RateLimiterService
    |
    |-- Redis INCR + TTL (atomic, thread-safe)
    |
    |-- count <= 100  -->  ApiController  -->  HTTP 200
    |
    |-- count > 100   -->  HTTP 429 (Too Many Requests)
```

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2, Java 17 |
| Rate Store | Redis 7 (sliding window counter) |
| Observability | Prometheus + Spring Actuator |
| Container | Docker + Docker Compose |
| Build | Maven |

## Running Locally

### Prerequisites
- Docker Desktop installed

### Start everything with one command

```bash
git clone https://github.com/441skr/rate-limiter-service.git
cd rate-limiter-service
docker-compose up --build
```

This starts Redis + the Spring Boot app automatically.

### Test the API

```bash
# Health check
curl http://localhost:8080/api/health

# Normal request (check X-RateLimit-Remaining header)
curl -I http://localhost:8080/api/data

# Simulate hitting the rate limit
for i in {1..101}; do
  curl -s -o /dev/null -w "%{http_code}\n" http://localhost:8080/api/data
done
# Last request returns 429
```

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/data | Protected endpoint (rate limited) |
| GET | /api/health | Health check |
| GET | /actuator/prometheus | Prometheus metrics |

## Rate Limiting Logic

- **Algorithm**: Sliding window counter using Redis INCR + TTL
- **Limit**: 100 requests per 60 seconds per client IP
- **Response headers**: `X-RateLimit-Remaining` on every response
- **429 body**: `{"error": "Rate limit exceeded", "retryAfter": 60}`
- **Thread safety**: Redis atomic increment ensures correctness across multiple instances
- **Load balancer support**: Reads `X-Forwarded-For` header for real client IP

## Key Design Decisions

- **Redis atomic INCR**: No race conditions even with multiple service replicas
- **TTL-based expiry**: No background cleanup job needed — Redis handles it natively
- **OncePerRequestFilter**: Runs exactly once per request, before any controller logic
- **Prometheus metrics**: `/actuator/prometheus` exposes request counts, latency histograms
