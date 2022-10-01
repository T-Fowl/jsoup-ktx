rootProject.name = "jsoup-ktx"

include("lib")
project(":lib").name = "jsoup-ktx"

include("ktor")
project(":ktor").name = "jsoup-ktx-ktor"

include("okhttp")
project(":okhttp").name = "jsoup-ktx-okhttp"
