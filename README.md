# Multi-User Chatting App

A student project for real-time multi-user chat functionality.
As a regular user, you can program a future channel where discussions will happen and invite your friends to chat in it. 
The app will support multiple channels and users chatting simultaneously.
And as an admin, you can manage users (ban/deactivate).

## 🚀 Quick Setup

1. **Clone the repo**

2. **Install dependencies** (Maven/Java)

3. **Database Setup** → See [Database Setup](#database-setup--sync) below

4. **Run the app**

## 📋 Features (Future Contributions)

Coming soon...

## 🗄️ Database Setup & Sync

### First Time Setup (After `git clone`)

**Option 1: Script (2 seconds)**
```bash
cd your-project-folder
./db/setup.sh
```

**Option 2: IntelliJ (No Terminal)**
1. **Database** tab → `+` → **SQLite** → **File** → Select `./chat_project.sqlite`
2. Right-click `chat_project.sqlite` → **Run SQL Script** → `db/db-setup.sql` → **Run**
3. Refresh Database tab → Done!

### Refresh After `git pull` (New Data Added)

After that the database is updated (i.e. update made to `setup.sql`), run:
```bash
./db/setup.sh
```
It replaces your DB with the latest team version.

### How It Works
- `db/db-setup.sql` = **one text file** with all tables + test data
- `db/setup.sh` = **magic button** that rebuilds your `.sqlite` from that text file
- Everyone gets **identical database state**
