# SimpleConfig 📦

A lightweight and easy-to-use configuration library for Java supporting **JSON & YAML** with dot-path access.

---

## 🚀 JitPack

[![](https://jitpack.io/v/nyxintrus/SimpleConfig.svg)](https://jitpack.io/#nyxintrus/SimpleConfig)

---

## 📥 Installation

### Gradle (JitPack)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.nyxintrus:SimpleConfig:TAG'
}
```

---

### Maven (JitPack)

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.nyxintrus</groupId>
    <artifactId>SimpleConfig</artifactId>
    <version>TAG</version>
</dependency>
```

---

## ⚡ Features

- 🧠 Simple dot-path system (`user.1.name`)
- 📄 JSON & YAML support
- 💾 Auto-save support
- 🔧 Easy API (no boilerplate)
- 🚀 Lightweight & fast
- 🧩 Extensible architecture

---

## 📦 Usage

### Load config

```java
Config config = Config.load("config.json");
```

or

```java
Config config = Config.load("config.yml");
```

---

### Set values

```java
config.set("user.1.name", "Niko");
config.set("user.1.age", "90");
```

---

### Get values

```java
String name = config.getString("user.1.name", "default");
int age = config.getInt("user.1.age", 0);
```

---

### Save config

```java
config.save();
```

---

## 📄 Example JSON output

```json
{
  "user": {
    "1": {
      "name": "Niko",
      "age": "90"
    }
  }
}
```

---

## 📄 Example YAML output

```yaml
user:
  "1":
    name: Niko
    age: "90"
```

---

## 🧠 Why SimpleConfig?

Most config libraries are:
- too heavy
- too complex
- or require too much boilerplate

SimpleConfig focuses on:

> Do one thing, but do it simple.

---

## 🛠 Roadmap

- Auto reload system
- Annotation-based config
- List support improvements
- Better type system
- YAML comments support

---

## 🤝 Contributing

Pull requests are welcome.

---

## 📜 License

MIT License
