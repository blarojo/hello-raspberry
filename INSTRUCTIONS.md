# Raspberry Pi 3 — Spring Boot Hello World

## Overview

This guide walks you through:
1. Setting up a brand new Raspberry Pi 3
2. Creating a Spring Boot application on your dev machine
3. Deploying and running the app on the Pi

---

## Part 1: Set Up the Raspberry Pi 3

### 1.1 Flash the OS

1. Download **Raspberry Pi Imager** from https://www.raspberrypi.com/software/
2. Insert a microSD card (8GB minimum, 16GB+ recommended) into your computer
3. Open Raspberry Pi Imager and choose:
   - **Device**: Raspberry Pi 3
   - **OS**: **Raspberry Pi OS (64-bit)** — the default top-level option, includes the desktop environment
     > This is the full version with a graphical desktop. Useful if you plan to connect a monitor later. For now you'll still manage the Pi over SSH; the desktop just sits idle until you need it.
   - **Storage**: your microSD card
4. Click the **gear icon** (Advanced Options) before writing:
   - Enable SSH
   - Set a username and password (e.g., `pi` / your chosen password)
   - Set your Wi-Fi SSID and password (so the Pi connects automatically)
   - Set hostname (e.g., `raspberrypi`)
5. Click **Write** and wait for it to finish

### 1.2 First Boot

1. Insert the microSD into the Pi, connect power
2. Wait ~90 seconds for first boot to complete
3. Find the Pi's IP address — check your router's admin page, or use:
   ```
   ping blancapi.local
   ```
4. SSH into the Pi:
   ```
   ssh blarojo@blancapi.local
   ```
   (or use the IP address if `.local` doesn't resolve)

### 1.3 Update the System

```bash
sudo apt update && sudo apt upgrade -y
```

### 1.4 Install Java

Raspberry Pi OS ships without Java. Install OpenJDK 21 — the current LTS, and the minimum required by Spring Boot 4:

```bash
sudo apt install -y openjdk-21-jdk
java -version
```

You should see output like `openjdk version "21.x.x"`.

---

## Part 2: Create the Spring Boot Application (on your dev machine)

### 2.1 Prerequisites on Your Dev Machine

- Java 21+ installed
- Maven or Gradle installed (Maven used in this guide)
- An IDE (IntelliJ IDEA, VS Code, etc.)

### 2.2 Generate the Project

Go to https://start.spring.io and configure:

| Field        | Value                        |
|--------------|------------------------------|
| Project      | Maven                        |
| Language     | Java                         |
| Spring Boot  | 4.0.4                        |
| Group        | com.example                  |
| Artifact     | hello-raspberry              |
| Packaging    | Jar                          |
| Java         | 21                           |
| Dependencies | Spring Web                   |

Click **Generate** and unzip into `C:\Dev\raspberry\hello-raspberry` (your current working directory).

### 2.3 Write the Hello World Controller

Edit `src/main/java/com/example/helloraspberry/HelloRaspberryApplication.java` — it will already have a `main` method. Add a REST controller in the same package.

Create `src/main/java/com/example/helloraspberry/HelloController.java`:

```java
package com.example.helloraspberry;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello from Raspberry Pi!";
    }
}
```

### 2.4 Configure the Port (Optional)

By default Spring Boot runs on port 8080. To change it, edit `src/main/resources/application.properties`:

```properties
server.port=8080
```

### 2.5 Build the Fat JAR

```bash
./mvnw clean package -DskipTests
```

This creates `target/hello-raspberry-0.0.1-SNAPSHOT.jar` — a self-contained executable JAR.

### 2.6 Test Locally First

```bash
java -jar target/hello-raspberry-0.0.1-SNAPSHOT.jar
```

Open http://localhost:8080 — you should see `Hello from Raspberry Pi!`

---

## Part 3: Deploy to the Raspberry Pi

### 3.1 Copy the JAR to the Pi

From your dev machine:

```bash
scp target/hello-raspberry-0.0.1-SNAPSHOT.jar blarojo@blancapi.local:/home/blarojo/
```

### 3.2 Run the App on the Pi

SSH into the Pi:

```bash
ssh blarojo@blancapi.local
```

Run the app:

```bash
java -jar /home/blarojo/hello-raspberry-0.0.1-SNAPSHOT.jar
```

### 3.3 Access the App

From any device on the same network, open a browser and navigate to:

```
http://blancapi.local:8080
```

or using the Pi's IP address:

```
http://<pi-ip-address>:8080
```

You should see: **Hello from Raspberry Pi!**

---

## Part 4: Run the App Automatically on Boot (Optional but Recommended)

So the app starts automatically when the Pi powers on:

### 4.1 Create a systemd Service

On the Pi:

```bash
sudo nano /etc/systemd/system/hello-raspberry.service
```

Paste:

```ini
[Unit]
Description=Hello Raspberry Spring Boot App
After=network.target

[Service]
User=blarojo
ExecStart=/usr/bin/java -jar /home/blarojo/hello-raspberry-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

### 4.2 Enable and Start the Service

```bash
sudo systemctl daemon-reload
sudo systemctl enable hello-raspberry
sudo systemctl start hello-raspberry
```

Check it's running:

```bash
sudo systemctl status hello-raspberry
```

### 4.3 View Logs

```bash
journalctl -u hello-raspberry -f
```

---

## Part 5: Re-Deploying After Changes

Each time you update the app:

1. On dev machine — rebuild:
   ```bash
   ./mvnw clean package -DskipTests
   ```
2. Copy to Pi:
   ```bash
   scp target/hello-raspberry-0.0.1-SNAPSHOT.jar blarojo@blancapi.local:/home/blarojo/
   ```
3. Restart the service on the Pi:
   ```bash
   sudo systemctl restart hello-raspberry
   ```

---

## Summary

| Step | What |
|------|------|
| Flash SD card | Raspberry Pi Imager with SSH + Wi-Fi pre-configured |
| First boot | SSH in, update, install Java 21 |
| Dev machine | Generate project at start.spring.io, write controller, build JAR |
| Deploy | `scp` the JAR, run with `java -jar` |
| Autostart | systemd service so app survives reboots |
