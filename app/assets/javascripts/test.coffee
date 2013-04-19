window.TimeController = ($scope , $timeout) ->
  getTime($scope)
  $timeout((
    update = ->
      getTime($scope)
      $timeout(update , 1000)
    ), 1000)

window.TaskController = ($scope , $http) ->
  $http.get("/task/all").success((data) ->
    $scope.tasks = data
  ).error((data) ->
    alert data
  )

  $scope.add = ->



getTime = ($scope) ->
  $.get("/time",(data) -> $scope.time = data)
