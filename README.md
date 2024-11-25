### This README.md and other documentation is specific to a branch / release

---

[![Build](https://github.com/verifyica-team/pipelines/actions/workflows/build.yaml/badge.svg)](https://github.com/verifyica-team/pipelines/actions/workflows/build.yaml) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/b908266740664e8c9985be70babe9262)](https://app.codacy.com/gh/verifyica-team/pipeliner/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

# Verifyica Pipeliner

Verifyica Pipeliner allows you define and run a local pipeline using a syntax ***similar*** to GitHub actions.

**Pipeliner is not designed to be 100% GitHub action compatible**

## Why ?

There are many scenarios where you need to perform various processing steps during development, system maintenance, or general workflows.

Example:

- patching source files
- setting up a test environment
- building a custom zip or tar.gz file

#### Maven & Gradle

Maven and Gradle ***can*** and ***are*** useful in development environment ... but usage and configuration of those tools requires a good knowledge of the specific tool.

#### Bash

A common go to is to use a Bash script/scripts ... they have there place.

Implementing logging, exit code checking, etc. is commonly implemented incorrectly.

#### Pipeliner

Pipeliner allows an easily declarative definition of a pipeline/pipelines using YAML.

## Pipeline YAML definition

Basic example:

```yaml
pipeline:
  name: hello-world-pipeline
  jobs:
    - name: hello-world-job
      enabled: true
      steps:
        - name: hello-world-step-1
          enabled: true
          run: echo "Hello World"
        - name: hello-world-step-2
          enabled: true
          run: echo \"Hello World\"
```

Other examples:

- [tests](tests) test pipelines

Advanced examples:

- [package.yaml](package.yaml) builds the packages
- [release.yaml](release.yaml) builds the release

## Pipeliner Output

Pipeliner uses various prefixes to indicate output.

### Info

- Information output is prefixed with `@info`

### Error

- Error output is prefixed with `@error`

### Trace

- Trace output is prefixed with `@trace`

### Pipeline, Job, Step

The pipeline, jobs, and steps output is prefixed with `@<IDENTIFIER>`

- `@pipeline`
- `@job`
- `@step`

**Notes**

- When a pipeline, jobs, and steps complete, an exit code and execution time in milliseconds is included


- The all jobs and all steps generate output, regardless if they are enabled or disabled

Starting output ...

```shell
@pipeline name=[hello-world-pipeline] id=[pipeline] ref=[pipeline] enabled=[true]
@job name=[hello-world-job] id=[pipeline-job-1] ref=[pipeline-job-1] enabled=[true]
@step name=[hello-world-step-1] id=[pipeline-job-1-step-1] ref=[pipeline-job-1-step-1] enabled=[true]
```

Finished output ...

```shell
@step name=[hello-world-step-2] id=[pipeline-job-1-step-2] ref=[pipeline-job-1-step-2] enabled=[true] exit-code=[0] ms=[6]
@job name=[hello-world-job] id=[pipeline-job-1] ref=[pipeline-job-1] enabled=[true] exit-code=[0] ms=[49]
@pipeline name=[hello-world-pipeline] id=[pipeline] ref=[pipeline] enabled=[true] exit-code=[0] ms=[49]
```

### Command

- The command executed is prefixed with `$ `
- The command output is prefixed with `> `

### Example Output

```shell
user@machine> ./pipeliner examples/hello-world-pipeline.yaml
```

```shell
@info Verifyica Pipeliner 0.4.1-post (https://github.com/verifyica-team/pipeliner)
@info filename=[examples/hello-world-pipeline.yaml]
@pipeline name=[hello-world-pipeline] id=[pipeline] ref=[pipeline] enabled=[true]
@job name=[hello-world-job] id=[pipeline-job-1] ref=[pipeline-job-1] enabled=[true]
@step name=[hello-world-step-1] id=[pipeline-job-1-step-1] ref=[pipeline-job-1-step-1] enabled=[true]
$ echo "Hello World"
> Hello World
@step name=[hello-world-step-1] id=[pipeline-job-1-step-1] ref=[pipeline-job-1-step-1] enabled=[true] exit-code=[0] ms=[37]
@step name=[hello-world-step-2] id=[pipeline-job-1-step-2] ref=[pipeline-job-1-step-2] enabled=[true]
$ echo \"Hello World\"
> "Hello World"
@step name=[hello-world-step-2] id=[pipeline-job-1-step-2] ref=[pipeline-job-1-step-2] enabled=[true] exit-code=[0] ms=[6]
@job name=[hello-world-job] id=[pipeline-job-1] ref=[pipeline-job-1] enabled=[true] exit-code=[0] ms=[49]
@pipeline name=[hello-world-pipeline] id=[pipeline] ref=[pipeline] enabled=[true] exit-code=[0] ms=[49]
```

## Project Installation

Zip:

```bash
cd <PROJECT DIRECTORY>
unzip verifyica-pipeliner.zip
./pipeliner --version
./pipeliner .pipeliner/hello-world-pipeline.yaml
```

Tarball:

```bash
cd <PROJECT DIRECTORY>
tar -xf verifyica-pipeliner.tar.gz
./pipeliner --version
./pipeliner .pipeliner/hello-world-pipeline.yaml
```

## Executing

```shell
./pipeliner <YOUR PIPELINE YAML>
```

# Pipeliner Options

Pipeliner has 7 options:

- `--version`
  - shows the version
  - can be used with `--minimal` for just the build number
- `--timestamps`
  - include timestamps in output
- `--log`
  - log to a file
- `--trace`
  - include trace messages in output
- `--minimal`
  - only include commands, command output, and errors in output
- `-P<property name>=<value>`
  - sets a property
  - repeatable
- `-E<environment variable>=<value>`
  - sets an environment variable
  - repeatable

Optionally, some options can be set using environment variables:

- `PIPELINER_TIMESTAMPS=true`
- `PIPERLINER_LOG=true`
- `PIPELINER_TRACE=true`
- `PIPELINER_MINIMAL=true`

**Notes**

- Command line options override environment variables

# Building

```bash
git clone https://github.com/verifyica-team/pipeliner
cd pipeliner
./mvnw clean package
./pipeliner package.yaml
```

## Packages

The `output` directory will contain the release packages and associated SHA1 checksum files.

- `verifiyica-piperliner.zip`
- `verifiyica-piperliner.zip.sha1`
- `verifyica-piperliner.tar.gz`
- `verifyica-piperliner.tar.gz.sha1`

# Experimental

An additional **experimental** tool `converter` has been created to easily convert a text file of shell commands to a pipeline YAML file.

See [Converter](CONVERTER.md) for details.

# Contributing

See [Contributing](CONTRIBUTING.md) for details.

# License

Apache License 2.0, see [LICENSE](LICENSE).

# DCO

See [DCO](DCO.md) for details.

# Support

![YourKit logo](https://www.yourkit.com/images/yklogo.png)

[YourKit](https://www.yourkit.com/) supports open source projects with innovative and intelligent tools for monitoring and profiling Java and .NET applications.

YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>,
<a href="https://www.yourkit.com/dotnet-profiler/">YourKit .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

---

Copyright (C) 2024-present Pipeliner project authors and contributors