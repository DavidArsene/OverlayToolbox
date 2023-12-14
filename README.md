# Overlay Toolbox

[ROOT REQUIRED] Manage everything overlay related on your device.

## Features

- Create, delete, enable, and disable overlays
- View overlay contents (NEW!)
- Share and import overlays (NEW!)
- Create overlays with strings (NEW!, Android 14+)

## Building

Requires a custom `android.jar` from [here](https://github.com/Reginer/aosp-android-jar) 
to be placed in `$ANDROID_HOME/platforms/android-34`

## Planned Features

Support for creating non-fabricated overlays

## Credits

@zacharee/[FabricateOverlay](https://github.com/zacharee/FabricateOverlay) - Design and code
@DerTyp7214/[Overlayer](https://github.com/DerTyp7214/Overlayer) - Design and code
@MuntashirAkon/[AppManager](https://github.com/MuntashirAkon/AppManager) - Design and code
@jacopotediosi/[GAppsMod](https://github.com/jacopotediosi/GAppsMod) - Design

## Known Issues

- Cannot currently set dimen values in Android 14 due to [a bug in AOSP](https://issuetracker.google.com/issues/306002254).
- Root-enabled file manager required for viewing non-fabricated overlays and receiving shared fabricated overlays.
- App sometimes starts in the background and displays a "superuser granted" toast.
