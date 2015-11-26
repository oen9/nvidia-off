# nvidia-off

Simple but very "fat" temporary solution to https://github.com/Bumblebee-Project/Bumblebee/issues/699

```
rmmod nvidia_modeset
rmmod nvidia
echo OFF > /proc/acpi/bbswitch
```
# Requirements to build
- JDK 8
- gradle

# Build
```
gradle build
```

# If your start-stop-daemon doesn't handle --no-close
1. Remove --no-close from builded app (~138 line)
2. If your editor added empty line on end of file
```
perl -pe 'chomp if eof' nvidia-off-0.1.jar > nvidia-off-fixed
chmod +x nvidia-off-fixed
chown root nvidia-off-fixed
```

# OpenRC init script
```
#!/sbin/runscript

start() {
  ebegin "Starting nvidia-off"
  /foo/bar/nvidia-off-fixed start
  eend $?
}

stop() {
  ebegin "Stopping nvidia-off"
  /foo/bar/nvidia-off-fixed stop
  eend $?
}
```
