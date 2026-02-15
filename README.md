# 🌍 TemplateWorld

TemplateWorld is an [AllayMC](https://github.com/AllayMC/Allay) plugin that allows you to create worlds based on template worlds. It supports both **temporary** (runtime-only) and **persistent** worlds, making it ideal for mini-game servers, lobby systems, and any scenario where you need to create worlds from pre-defined templates. 🎮

## ✨ Features

- 🗺️ Create temporary or persistent worlds from pre-defined templates
- 💨 Temporary worlds are runtime-only and won't persist to disk
- 💾 Persistent worlds preserve modifications using copy-on-write storage
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

#### 💨 Temporary Worlds

Temporary worlds are runtime-only — they get a random UUID as their name and are automatically cleaned up when unloaded or on server restart.

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
```

#### 💾 Persistent Worlds

Persistent worlds are not runtime-only — their modifications are preserved across chunk unloads. Reads check the persistent storage first, then fall back to the template for unmodified chunks.

```java
import org.allaymc.templateworld.TemplateWorld;
import org.allaymc.api.world.World;

// Create a persistent world using default LEVELDB format
World world = TemplateWorld.createPersistentWorld(
    "bedwars_map1",  // template name
    "my_game_room"   // persistent world name
);

// Create with custom formats
World world = TemplateWorld.createPersistentWorld(
    "bedwars_map1",  // template name
    "LEVELDB",       // template format
    "my_game_room",  // persistent world name
    "LEVELDB"        // persistent world format
);
```

### ⚙️ How It Works

1. 📂 Template worlds are stored in the `templates/` directory
2. 📖 When a world is created, chunks are read from the template but written to a separate storage location
3. 💨 **Temporary worlds**: written to `worlds/.tmp/`, each gets a unique UUID as its name, and are marked as `runtimeOnly`
4. 💾 **Persistent worlds**: written to `worlds/<worldName>/`, modifications are preserved via copy-on-write — unmodified chunks are read from the template, modified chunks are read from the persistent storage
5. 🔄 On server restart, all temporary world files are automatically cleaned up, while persistent worlds retain their data

## 📄 License

This project is licensed under the LGPL v3 License - see the [LICENSE](LICENSE) file for details.
