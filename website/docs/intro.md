---
sidebar_position: 1
slug: /
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

# 시작하기

GraphQL Jetpack은 [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin) 과 [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc) 를 사용하여 GraphQL Relay 스펙을 가진 서버를 쉽게 작성할 수 있게 도와줍니다.

### Spring Data GraphQL

순차 증가하는 ID를 릴레이의 유니크한 ID로 자동 변환합니다. 또한 노드 쿼리 및 커넥션 페이지를 위한 레포지토리를 지원합니다.

### GraphQL Kotlin Spring Security

스프링 시큐리티 스타일의 인증 인가를 처리할 수 있습니다. `@Auth` 어노테이션은 GraphQL의 `@auth` 디렉티브와 매핑됩니다. 

### GraphQL Kotlin Upload

[그래프큐엘 멀티파트 요청 스팩](https://github.com/jaydenseric/graphql-multipart-request-spec)의 [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/) 구현체 입니다

# 설치

<Tabs
defaultValue="gradle"
values={[
{ label: 'Gradle Kotlin', value: 'gradle' },
{ label: 'Maven', value: 'maven' }
]
}>

<TabItem value="gradle">

```kotlin
repositories { 
    maven("https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo")
}

dependencies {
    /* GraphQL Data R2DBC */
    implementation("io.github.wickedev", "spring-data-graphql-r2dbc-starter", latestVersion)
    /* GraphQL Kotlin Spring Security + GraphQL Kotlin Upload */
    implementation("io.github.wickedev", "graphql-jetpack-starter", latestVersion)
}
```

</TabItem>
<TabItem value="maven">

```xml
<project>
    <repositories>
        <repository>
          <id>graphql-jetpack-repo</id>
          <url>https://github.com/wickedev/graphql-jetpack/raw/deploy/maven-repo</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>io.github.wickedev</groupId>
          <artifactId>spring-data-graphql-r2dbc-starter</artifactId>
            <version>${latestVersion}</version>
        </dependency>
        <dependency>
            <groupId>io.github.wickedev</groupId>
            <artifactId>graphql-jetpack-starter</artifactId>
            <version>${latestVersion}</version>
        </dependency>
    </dependencies>
</project>
```

</TabItem>
</Tabs>

# Tutorial Intro

Let's discover **Docusaurus in less than 5 minutes**.

## Getting Started

Get started by **creating a new site**.

Or **try Docusaurus immediately** with **[docusaurus.new](https://docusaurus.new)**.

### What you'll need

- [Node.js](https://nodejs.org/en/download/) version 14 or above:
  - When installing Node.js, you are recommended to check all checkboxes related to dependencies.

## Generate a new site

Generate a new Docusaurus site using the **classic template**.

The classic template will automatically be added to your project after you run the command:

```bash
npm init docusaurus@latest my-website classic
```

You can type this command into Command Prompt, Powershell, Terminal, or any other integrated terminal of your code editor.

The command also installs all necessary dependencies you need to run Docusaurus.

## Start your site

Run the development server:

```bash
cd my-website
npm run start
```

The `cd` command changes the directory you're working with. In order to work with your newly created Docusaurus site, you'll need to navigate the terminal there.

The `npm run start` command builds your website locally and serves it through a development server, ready for you to view at http://localhost:3000/.

Open `docs/intro.md` (this page) and edit some lines: the site **reloads automatically** and displays your changes.
