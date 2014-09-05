Finagle memcache hbase test1
============================

Example of service based on finagle memcached protocol to aggregate time series stream of data into (min, avg, max) into hbase

Usage
=====

1. install hbase docker instance using [dajobe hbase 0.94.11 docker instance](https://github.com/dajobe/hbase-docker).
2. build you [finagle](https://github.com/twitter/finagle)
   [6.20.0](https://github.com/twitter/finagle/archive/6.20.0.zip) and publish it locally into ivy2 / mvn.
   preinstall `thrift` compiler first.
3. install `metrics` table using `data/hbase.schema`. put it into your `hbase shell`
4. build `./sbt assembly`
5. run
6. put something in the system using memcached protocol

What is that
============

Memcached protocol frontend to obtain and aggregate streams of data to write it down to the hbase.

How to build finagle
====================

    $ git clone https://github.com/twitter/finagle
    $ cd finagle
    $ git co 6.20.0
    - fix scala compiler version to 2.10.4 in Build.scala
    $ ./sbt compile publishLocal publishM2

How to build HPaste (do not needed rn)
======================================

    - get a source
    - fix scala version to 2.10.4 in pom.xml
    - disable tests before build. add <skipTests>true</skipTests> to surefire configuration.

    $ mvn clean install

Use this SBT plugin to publish artifacts to ivy2 local cache: [sbt-maven-plugin](https://github.com/shivawu/sbt-maven-plugin)

plugins.sbt:

    addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.3-SNAPSHOT")

    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

build.sbt

    resolvers += "Thrift 0.2 is here" at "http://people.apache.org/~rawson/repo/"

publish

    $ ./sbt publishLocal

Building
========

    $ git clone https://github.com/sitano/finagle-memcache-hbase-test1
    $ cd finagle-memcache-hbase-test1
    $ ./sbt assembly

The 'fat jar' is now available as:

    target/finagle-memcache-hbase-test1-0.0.1.jar

Run
===

    $ java -jar ./target/scala-2.10/finagle-memcache-hbase-test1-0.0.1.jar

Put data into it
================

    printf "set cpu0.usage 0 0 2\n12\n" | nc 127.0.0.1 11211
    printf "set cpu0.usage 0 0 2\n24\n" | nc 127.0.0.1 11211
    printf "set cpu1.usage 0 0 1\n2\n" | nc 127.0.0.1 11211

Unit testing
============

The `assembly` command above runs the test suite - but you can also run this manually with:

    $ ./sbt test

Get IDEA project
================

    $ ./sbt gen-idea

See also
========

* https://github.com/twitter/finagle
* http://wiki.apache.org/hadoop/Hbase/Shell
* http://opentsdb.net/docs/build/html/user_guide/backends/hbase.html
* http://hbase.apache.org/book/rowkey.design.html
* http://hbase.apache.org/book/schema.casestudies.html

Copyright and license
=====================

The MIT License (MIT)

Copyright (c) 2014 Ivan Prisyazhniy <john.koepi@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
