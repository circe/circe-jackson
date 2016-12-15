# circe-jackson

[![Build status](https://img.shields.io/travis/circe/circe/master.svg)](https://travis-ci.org/circe/circe-jackson)
[![Coverage status](https://img.shields.io/codecov/c/github/circe/circe/master.svg)](https://codecov.io/github/circe/circe-jackson)
[![Gitter](https://img.shields.io/badge/gitter-join%20chat-green.svg)](https://gitter.im/circe/circe)
[![Maven Central](https://img.shields.io/maven-central/v/io.circe/circe-core_2.11.svg)](https://maven-badges.herokuapp.com/maven-central/io.circe/circe-jackson_2.11)

This project provides support for using [Jackson][jackson] for JSON parsing and printing with
[circe][circe], a Scala library for encoding and decoding JSON to Scala types.

Several versions of Jackson are still in widespread use, and `io.circe.jackson` is cross-published
for 2.5, 2.6, 2.7, and 2.8. The artifact supporting the most recent Jackson version is always named
`circe-jackson`, while older versions have a two digit suffix on the artifact name
(`circe-jackson25`, `circe-jackson26`, etc.).

The project source is mostly shared, with version-specific code in separate source trees. Note that
the source supporting Jackson 2.6 and 2.7 is identical, so these version share a source tree.

## Contributors and participation

All circe projects support the [Typelevel][typelevel] [code of conduct][code-of-conduct] and we want
all of their channels (Gitter, GitHub, etc.) to be welcoming environments for everyone.

Please see the [circe contributors' guide](contributing) for details on how to submit a pull
request.

## License

circe-jackson is licensed under the **[Apache License, Version 2.0][apache]**
(the "License"); you may not use this software except in compliance with the
License.

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

[apache]: http://www.apache.org/licenses/LICENSE-2.0
[circe]: https://github.com/circe/circe
[code-of-conduct]: http://typelevel.org/conduct.html
[jackson]: https://github.com/FasterXML/jackson
[typelevel]: http://typelevel.org/
