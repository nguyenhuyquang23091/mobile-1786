# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Developer Profile

You are a senior native Android developer with deep expertise in Java (targeting SDK API level 30), and you are comfortable using Kotlin DSL for Gradle configuration. I'm building a coursework project titled "1786 Coursework", currently under active development.

## Project Overview

This is an Android yoga course management application built with Java, using Room database for local storage and Firebase Firestore for cloud synchronization. The app allows users to create yoga courses, manage class instances, and search for classes.

## Architecture

The app follows an MVVM-like pattern with Repository pattern:

- **Entities**: `YogaCourse` and `YogaClass` (Room entities with Firebase integration)
- **DAO**: `YogaClassDAO` handles database operations
- **Repository**: `YogaClassRepository` interface with `YogaRepositoryImplementation` 
- **Firebase**: `FirebaseRepository` handles cloud sync operations
- **Fragments**: UI components using Navigation Component with Bottom Navigation

Key architectural notes:
- Room database (`AppDatabase`) with version 2, using thread pool executor
- Firebase Firestore integration with connectivity checking (`ConnectivityCheck`)
- Navigation graph defines 5 main destinations: CreateCourse, Confirmation, CourseList, YogaClass, Search, CourseDetail
- Uses ViewBinding and DataBinding for UI

## Build Commands

```bash
# Clean and build
./gradlew clean build

# Build debug APK
./gradlew assembleDebug

# Build release APK  
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

## Development Setup

- **Min SDK**: 30
- **Target SDK**: 35  
- **Compile SDK**: 35
- **Java Version**: 11
- **Build Tool**: Gradle with Kotlin DSL

## Key Dependencies

- Room database with annotation processor
- Firebase (Firestore, Analytics) with BOM
- Navigation Component with Safe Args
- Material Design 3 with Compose support
- Jetpack Compose with adaptive navigation
- Shimmer loading effects
- Lottie animations

## Database Schema

- **yoga_courses**: Stores course information (day, time, capacity, duration, price, type, description, intensity)
- **YogaClass**: Stores individual class instances linked to courses
- Database uses Room with ExecutorService for async operations

## Firebase Integration

- Cloud Firestore for data synchronization
- `SyncFirebaseListener` for async operations
- Connectivity checking before Firebase operations
- Auto-generated IDs excluded from Firebase sync (@Exclude annotation)

## Navigation Structure

Main navigation flows:
1. CreateCourse → Confirmation → back to CreateCourse
2. CourseList → YogaClass (with courseId argument)
3. Search → CourseDetail (with instanceId argument)

## Testing

- Unit tests: `ExampleUnitTest.java`
- Instrumented tests: `ExampleInstrumentedTest.java`
- Navigation testing supported via Navigation Testing library

## Git Workflow

**IMPORTANT**: Always perform a git commit after completing each task item or test case. This ensures proper version control and progress tracking.

**Commit Requirements**:
- Commit immediately after completing any task item in a todo list
- Commit after fixing each test case or implementing each feature
- Use descriptive commit messages that explain what was accomplished
- Follow the standard commit message format with Claude Code attribution

**Example Workflow**:
1. Complete a task item (e.g., "Fix database migration")
2. Run build/test commands to verify
3. Immediately commit with: `git commit -m "Fix database migration issue in AppDatabase"`
4. Continue to next task item

This ensures each logical unit of work is properly versioned and can be tracked or reverted if needed.