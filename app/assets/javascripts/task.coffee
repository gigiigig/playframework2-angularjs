window.TimeController = ($scope , $timeout) ->
  getTime($scope)
  $timeout((
    update = ->
      getTime($scope)
      $timeout(update , 1000)
    ), 1000)

window.TaskController = ($scope , $http , $timeout) ->
  updateList = ->
    $http.get("/task/").success((data) ->
      $scope.tasks = data
    )

  updateList()

  $scope.add = ->
    $http.post("/task/" , $scope.task).success( ->
      $scope.message = "saved"
      updateList()
    )

  $scope.start = (task) ->
    task.started = true

  $scope.stop = (task) ->
    task.started = false

  $scope.delete = (task) ->
    $http.delete("/task/#{task.id}").success( ->
      $scope.message = "deleted task #{task.id}"
      updateList()
    )

  $scope.update = ->
    $http.put("/task/" , $scope.tasks).success((data) ->
      $scope.message = data
    )


  $timeout(increase = ->
    for t in $scope.tasks
      if(t.started) then t.startDate += 1000
    $timeout(increase , 1000)
  ,1000)

window.angular.module('myFilters' , []).filter('timer' , ->
  (input) ->
    seconds=addZero((input/1000)%60)
    minutes=addZero((input/(1000*60))%60)
    hours=addZero((input/(1000*60*60))%24)
    "#{hours}:#{minutes}:#{seconds}"
)

getTime = ($scope) ->
  $scope.time = new Date().getTime()

addZero = (value) ->
  if(value < 10)
    "0#{Math.round(value)}"
  else
    value