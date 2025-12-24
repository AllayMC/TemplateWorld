# 🌍 TemplateWorld

TemplateWorld is an [AllayMC](https://github.com/AllayMC/Allay) plugin that allows you to create runtime-only worlds based on template worlds. It is designed for mini-game servers where you need to create temporary game rooms that are automatically cleaned up after use. 🎮

## ✨ Features

- 🗺️ Create temporary worlds from pre-defined templates
- 💨 Runtime-only worlds that won't persist to disk
- 📦 Support for different world storage formats (LEVELDB, etc.)
- 🧹 Automatic cleanup of temporary world files on server restart
- 🔌 Simple API for programmatic world creation

## 📥 Installation

1. 📦 Download the latest release from [Releases](https://github.com/AllayMC/TemplateWorld/releases)
2. 📂 Place the JAR file in your server's `plugins` folder
3. 🚀 Start the server

## 🎯 Usage

### 📁 Preparing Templates

1. Create a folder named `templates` in your server root directory
2. Copy your world folder into `templates/`
3. The folder name will be used as the template name

```
server/
├── templates/
│   ├── lobby/
│   ├── bedwars_map1/
│   └── skywars_map1/
└── ...
```

### 💻 Commands

| Command                            | Description                              | Permission              |
|------------------------------------|------------------------------------------|-------------------------|
| `/template create <template_name>` | Create a temporary world from a template | `templateworld.command` |

### 🔧 API Usage

First, you should add the dependency to your project:

```kts
repositories {
    mavenCentral()
}

dependencies {
    compileOnly(group = "org.allaymc", name = "template-world", version = "0.1.0")
}
```

You can use the API to create temporary worlds programmatically:

```java
import org.allaymc.templateworld.TemplateWorld;
import org.allaymc.api.world.World;

// Create a temporary world using default LEVELDB format
World tmpWorld = TemplateWorld.createTmpWorld("bedwars_map1");

// Create with custom formats
World tmpWorld = TemplateWorld.createTmpWorld(
    "bedwars_map1",  // template name
    "LEVELDB",       // template format
    "LEVELDB"        // temporary world format
);

// The world is automatically marked as runtime-only
// and will be cleaned up when unloaded
```

### ⚙️ How It Works

1. 📂 Template worlds are stored in the `templates/` directory
2. 📖 When a temporary world is created, chunks are read from the template but written to a temporary location (`worlds/.tmp/`)
3. 🔑 Each temporary world gets a unique UUID as its name
4. ⏳ The world is marked as `runtimeOnly`, so it won't persist after unloading
5. 🔄 On server restart, all temporary world files are automatically cleaned up

## 📄 License

This project is licensed under the LGPL v3 License - see the [LICENSE](LICENSE) file for details.
