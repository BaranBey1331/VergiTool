#!/bin/sh

# Gradle Wrapper Script for Unix
# VergiAI - Üst Düzey Yazılım Mühendisi Standartı

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Gradle'ı bul ve çalıştır
if [ -z "$JAVA_HOME" ] ; then
    JAVACMD="java"
else
    JAVACMD="$JAVA_HOME/bin/java"
fi

exec "$JAVACMD" -Xmx1024m -Dorg.gradle.appname="$APP_BASE_NAME" -classpath "gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
