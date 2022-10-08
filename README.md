# jsoup-ktx

![Build](https://github.com/T-Fowl/jsoup-ktx/workflows/Build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/com.tfowl.jsoup/jsoup-ktx)
![GitHub](https://img.shields.io/github/license/T-Fowl/jsoup-ktx)

Extension functions for Jsoup objects and compatability for other HTTP libraries.

## Usage

TODO

```kotlin
// TODO
```

## Modules

`jsoup-ktx` base module  
`jsoup-ktx-ktor` ktor compatability  
`jsoup-ktx-okhttp` okhttp compatability  

## Download

Add a gradle dependency to your project:

Groovy
```groovy
repositories {
    mavenCentral()
}
implementation "com.tfowl.jsoup:jsoup-ktx:$version"

// Recommend overriding jsoup version
```

Kotlin DSL
```kotlin
repositories {
    mavenCentral()
}
implementation("com.tfowl.jsoup:jsoup-ktx:$version")
// Recommend overriding jsoup version
```

Add a maven dependency to your project:
```xml
<dependency>
  <groupId>com.tfowl.jsoup</groupId>
  <artifactId>jsoup-ktx</artifactId>
  <version>${version}</version>
</dependency>
<!-- Recommend overriding jsoup version -->
```

## License

```
MIT License

Copyright (c) 2022 Thomas Fowler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
