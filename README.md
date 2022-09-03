# Three simple modules to analyse the time of insertion using four different Redis libraries for Java/Spring:
- Redis OM Spring
- Spring Data Redis
- Jedis

In each specific TestRunner class you can configure the number of keys to insert and the number of runs to calculate the average.

For testing, we're using the model of an Airport. It has a size of around 2kb.

## Metrics
### Redis OM Spring:

#### Before version 0.6.0
|                    | Avg (ms) | Per Insert (ms) | Number Of Keys | Number of runs | 
|--------------------|----------|-----------------|----------------|----------------|
| Hash               | 2787     | 2.78            | 1000           | 10             |
| Json               | 2357     | 2.35            | 1000           | 10             |

#### After version 0.6.0
|                    | Avg (ms) | Per Insert (ms) | Number Of Keys | Number of runs | 
|--------------------|----------|-----------------|----------------|----------------|
| Hash               | 204      | 0.20            | 1000           | 10             |
| Json               | 261      | 0.26            | 1000           | 10             |


### Spring Data Redis
|                    | Avg (ms) | Per Insert (ms) | Number Of Keys | Number of runs | 
|--------------------|----------|-----------------|----------------|----------------|
| Hash               | 2772     | 2.77            | 1000           | 10             |

### Jedis:
|                    | Avg (ms) | Per Insert (ms) | Number Of Keys | Number of runs | 
|--------------------|----------|-----------------|----------------|----------------|
| Hash Non Pipelined | 609      | 0.60            | 1000           | 10             |
| Hash Pipelined     | 18.36    | 0.018           | 1000           | 10             |
| Json Pipelined     | 23.72    | 0.023           | 1000           | 10             |

### Lettuce:
|                    | Avg (ms) | Per Insert (ms) | Number Of Keys | Number of runs | 
|--------------------|----------|-----------------|----------------|----------------|
| Hash Non Pipelined | 1747     | 1.74            | 1000           | 10             |
| Hash Pipelined     | 13       | 0.013           | 1000           | 10             |