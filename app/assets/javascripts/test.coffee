window.TimeController = ($scope , $timeout) ->
  getTime($scope)
  $timeout((
    update = ->
      getTime($scope)
      $timeout(update , 1000)
    ), 1000)

getTime = ($scope) ->
  $.get("/time",(data) -> $scope.time = data)
