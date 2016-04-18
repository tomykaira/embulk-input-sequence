# Sequence input plugin for Embulk

Generate sequential number.

## Overview

* **Plugin type**: input
* **Resume supported**: no (TODO)
* **Cleanup supported**: yes
* **Guess supported**: no

## Configuration

- **from**: start value (integer, required)
- **to**: last value inclusive (integer, required)
- **step**: step (integer, default: `1`)

Step may be negative if `from > to`.

## Example

sample.yml:

```yaml
exec: {}

in:
  type: sequence
  from: 1
  to: 5
  step: 1

out:
  type: stdout
```

output:

```
1
2
3
4
5
```

## Build

```
$ ./gradlew gem  # -t to watch change of files and rebuild continuously
```
