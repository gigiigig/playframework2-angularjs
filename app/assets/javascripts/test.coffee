window.TimeController = ($scope , $timeout) ->
  getTime($scope)
  $timeout((
    update = ->
      getTime($scope)
      $timeout(update , 1000)
    ), 1000)

window.TaskController = ($scope , $http) ->
  updateList = ->
    $http.get("/task/all").success((data) ->
      $scope.tasks = data
    )

  updateList()

  $scope.add = ->
    $scope.task.id = ""
    $http.post("/task/" , $scope.task).success( ->
      $scope.message = "saved"
      updateList()
    )


getTime = ($scope) ->
  $.get("/time",(data) -> $scope.time = data)
