window.TaskController = ($scope , $http , $timeout) ->

  updateList = ->
    $http.get("/task/").success((data) ->
      $scope.tasks = data
    )

  updateList()

  $scope.start = (task) ->
    task.running = true

  $scope.stop = (task) ->
    task.running = false

  $scope.add = ->
    $scope.update()
    $http.post("/task/" , $scope.task).success((data) ->
      showMessage(data)
      updateList()
    )

  $scope.delete = (task) ->
    $scope.update()
    $http.delete("/task/#{task.id}").success((data) ->
      showMessage(data)
      updateList()
    )

  $scope.update = ->
    $http.put("/task/" , $scope.tasks).success((data) ->
      showMessage(data)
    )

  $timeout(increase = ->
    for t in $scope.tasks
      if(t.running) then t.startDate += 1000
    $timeout(increase , 1000)
  ,1000)

  showMessage = (data) ->
    $scope.message = data
    $timeout(( -> $scope.message = null), 3000)


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