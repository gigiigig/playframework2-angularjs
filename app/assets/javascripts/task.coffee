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

  $scope.prepareTimeEdit = (task) ->
    task.minutes = toMinutes(task.startDate)
    task.hours = toHours(task.startDate)
    task.seconds = toSeconds(task.startDate)

  $scope.updateTime = (task) ->
    task.startDate = toTime(parseInt(task.hours), parseInt(task.minutes), parseInt(task.seconds))
    $scope.update()
    updateList()


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
    seconds=toSeconds(input)
    minutes=toMinutes(input)
    hours=toHours(input)
    "#{hours}:#{minutes}:#{seconds}"
).filter('seconds', ->
  (input) ->
    addZero((input/1000)%60)
).filter('minutes', ->
  (input) ->
    addZero((input/(1000*60))%60)
).filter('hours', ->
  (input) ->
    addZero((input/(1000*60*60))%24)
)

getTime = ($scope) ->
  $scope.time = new Date().getTime()

addZero = (value) ->
  value = Math.round(value)
  if(value < 10)
    "0#{value}"
  else
    value

toHours = (time) ->
  addZero((time/(1000*60*60))%24)
toMinutes = (time) ->
  addZero((time/(1000*60))%60)
toSeconds = (time) ->
  addZero((time/1000)%60)

toTime = (hours,minutes,seconds) ->
  console.debug("hours : " + hours)
  console.debug("minutes : " + minutes)
  console.debug("seconds : " + seconds)

  toret = ((hours * 60 * 60) + (minutes * 60) + seconds) * 1000
  console.debug("toTime : " + toret)

  toret