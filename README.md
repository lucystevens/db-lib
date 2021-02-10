![Develop Status][workflow-badge-develop]
![Main Status][workflow-badge-main]
![Version][version-badge] 

# db-lib
**db-lib** is a library for managing database connections and ORM using hibernate

## Installation

Install the latest version of db-lib using Maven:

```	
<dependency>
	<groupId>uk.co.lukestevens</groupId>
	<artifactId>db-lib</artifactId>
	<version>2.0.0</version>
</dependency>
```

and the latest version of [base-lib][base-lib-repo]

### Github Packages Authentication
Currently public packages on Github require authentication to be installed by Maven. Add the following repository to your project's `.m2/settings.xml`

```
<repository>
	<id>github-lukecmstevens</id>
	<name>GitHub lukecmstevens Apache Maven Packages</name>
	<url>https://maven.pkg.github.com/lukecmstevens/packages</url>
	<snapshots><enabled>true</enabled></snapshots>
</repository>
```

For more information see here: [Authenticating with Github packages][gh-package-auth]

## Usage

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

New features, fixes, and bugs should be branched off of develop.

Please make sure to update tests as appropriate.

## License
[MIT][mit-license]

[base-lib-repo]: https://github.com/lukecmstevens/base-lib
[gh-package-auth]: https://docs.github.com/en/free-pro-team@latest/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages
[workflow-badge-develop]: https://img.shields.io/github/workflow/status/lukecmstevens/db-lib/publish/develop?label=develop
[workflow-badge-main]: https://img.shields.io/github/workflow/status/lukecmstevens/db-lib/release/main?label=main
[version-badge]: https://img.shields.io/github/v/release/lukecmstevens/db-lib
[mit-license]: https://choosealicense.com/licenses/mit/