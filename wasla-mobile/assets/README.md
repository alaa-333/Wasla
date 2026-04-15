# Assets

Static assets for the Wasla app.

## Structure

```
assets/
├── images/          # App images (logo, icons, etc.)
│   └── .gitkeep
├── icons/           # Custom icons
│   └── .gitkeep
└── README.md        # This file
```

## Usage

Add assets to `pubspec.yaml`:

```yaml
flutter:
  assets:
    - assets/images/
    - assets/icons/
```

## Important Notes

- ⚠️ **No user-uploaded images** - App is 100% text-based
- ✅ Only static app assets (logo, icons, etc.)
- ✅ Use standard Flutter asset loading: `AssetImage('assets/images/logo.png')`
