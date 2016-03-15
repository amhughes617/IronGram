lastPhotosData = null;

function getPhotos(photosData) {
    if (JSON.stringify(lastPhotosData) !== JSON.stringify(photosData)) {
        $("#photos").empty();
        for(var i in photosData) {
            var elem = $("<img>");
            elem.attr("src", photosData[i].fileName);
            $("#photos").append(elem);
            lastPhotosData = photosData;
        }
    }
}
function logout() {
    $.post(
        "/logout"
    );

}
function getUser() {
    $.get(
        "/user",
        function(userData) {
            if (userData.length == 0) {
                $("#photos").empty();
                $("#login").show();
                $("#upload").hide();
                $("#logout").hide();
            }
            else {
                $("#upload").show();
                $("#login").hide();
                $.get("/photos", getPhotos);
            }
        }
    );
}
getUser();
setInterval(getUser,1000);
