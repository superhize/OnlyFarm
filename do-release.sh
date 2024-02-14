#!/usr/bin/env bash
newversion="$1"
sed -i -E 's/(version *= *).*/\1"'"$newversion"'"/' build.gradle.kts
git add build.gradle.kts

git commit -m "Bump version"
git tag "$newversion"
git push origin main "$newversion"
./gradlew publishAllPublicationsToHizeRepository

pause
