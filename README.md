# Napknbook - Android App

A feature-rich Android application built with Java, featuring character management, skill progression, inventory systems, and social interactions.

## 📱 Overview

Napknbook is a comprehensive mobile application that combines RPG-like character progression with task management and social features. The app allows users to create and manage characters, develop skills, manage inventory, track tasks, and interact with other users through conversations and trading.

## ✨ Features

### Character Management
- Create and manage multiple characters
- Character progression and leveling system
- Character-specific skills and inventory
- Character generation and customization

### Skills System
- Skill tracking and progression
- Skill-based activities and challenges
- Visual skill cards and progress indicators
- Skill generation and management

### Task Management
- Create, edit, and organize tasks
- Task categories with custom organization
- Priority-based task sorting
- High-priority task filtering
- Task completion tracking
- Local and server synchronization

### Inventory System
- Item management and organization
- Character-specific inventories
- Inventory item details and tracking
- Item generation and management

### Social Features
- User profiles and leaderboards
- Conversations and comments system
- Trading functionality between users
- Connect with other players
- User activity tracking

### Media & Content
- Video playback with custom media controller
- Camera integration for photo capture
- Image loading and caching (Glide)
- Short-form content viewing

### Authentication & Security
- Email/password authentication
- Google Sign-In integration
- Encrypted local storage (EncryptedSharedPreferences)
- Secure token management
- Biometric authentication support

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Java
- **Platform**: Android (API 21+, Target API 34)
- **Architecture**: MVVM with Repository pattern

### Key Libraries & Frameworks
- **UI**: Material Design Components, ConstraintLayout, RecyclerView
- **Networking**: Retrofit 2.9.0, OkHttp
- **Database**: Room Database 2.6.1 (Local SQLite)
- **Image Loading**: Glide 4.16.0
- **Media**: Media3 ExoPlayer 1.1.1
- **Camera**: CameraX 1.3.0
- **Security**: AndroidX Security Crypto
- **Authentication**: Google Play Services Auth
- **Billing**: Google Play Billing Library 6.1.0

### Architecture Components
- **Database**: Room with DAOs and Repositories
- **ViewModels**: TaskViewModel, TaskCategoryViewModel
- **API**: RESTful API integration via Retrofit
- **Local Storage**: EncryptedSharedPreferences for sensitive data

## 📂 Project Structure

```
app/src/main/java/com/accelerate/napknbook/
├── activities/          # Main app activities
├── adapters/           # RecyclerView adapters
├── api/                # API service definitions
├── database/           # Room database, DAOs, repositories
├── models/             # Data models
├── utils/              # Utility classes
├── viewmodels/         # ViewModel classes
├── fragments/          # UI fragments
├── add/                # Add/create activities
└── edit/               # Edit activities
```

## 🔐 Security

- All sensitive credentials have been removed from this repository
- Encrypted local storage for user data
- Secure token-based authentication
- HTTPS-only network communication

## 📝 Note

This repository is for portfolio/educational purposes. The app requires backend API configuration and authentication credentials that are not included in this repository.

## 🚀 Building the Project

1. Clone the repository
2. Open in Android Studio
3. Configure `local.properties` with your signing configuration (see `local.properties.example`)
4. Note: The app requires backend API access and authentication credentials to function

## 📄 License

[Add your license here]
