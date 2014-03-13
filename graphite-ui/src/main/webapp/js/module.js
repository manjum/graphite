var searchModule = angular.module('graphite', ['ngRoute', 'ui.bootstrap']);
searchModule.factory('GraphiteQuery', function($http){
	return {
		data: function(queryString){
			return $http.get('/graphite-ui/rest/query/' + queryString)
					.then(function(result) {
						return result.data;
					});
		},
		expand: function(queryString){
			return $http.get('/graphite-ui/rest/query/expand/' + queryString)
					.then(function(result) {
						return result.data;
					});
		},
		results: function(queryString){
			return $http.get('/graphite-ui/rest/query/result/' + queryString)
					.then(function(result) {
						return result.data;
					});
		},
		connections: function(id){
			return $http.get('/graphite-ui/rest/connections/' + id + '/1/*')
					.then(function(result) {
						return {id : id, data: result.data};
					});
		}
	}
});

searchModule.config(function($routeProvider) {
	$routeProvider
		.when('/search/:searchString', {
			controller: 'SearchResultCtrl',
			templateUrl: 'list.html'
		});
});

searchModule.controller('SearchCtrl', function($scope, $location, GraphiteQuery) {
	$scope.expand = function(event){
		var text = $('ul.dropdown-menu li.active a.ng-scope').text();
		if(event.keyCode == 39){
			$scope.possibleQueries = function(text){
			    return GraphiteQuery.expand(text).then(function(data){
					return data.result;
				});
			};
		}else{
			$scope.possibleQueries = function(text){
			    return GraphiteQuery.data(text).then(function(data){
					return data.result;
				});
			};
			if(event.keyCode == 13 || event.keyCode == 9){
				$location.path('search/' + $scope.selectedQuery);				
			}
		}
	};
});

searchModule.controller('SearchResultCtrl', function($scope, $routeParams, GraphiteQuery) {
	$scope.results = [];
	$scope.connections = {};
	GraphiteQuery.results($routeParams.searchString).then(function(data){
		angular.copy(data.result, $scope.results);
		for(var i = 0; i < $scope.results.length; i++){
			GraphiteQuery.connections($scope.results[i].id).then(function(data){
				$.each(data.data.result, function(index, result){
					if($scope.connections[result.strength] == undefined)
						$scope.connections[result.strength] = [];
					$scope.connections[result.strength].push(result);
				});
			});
		}
		return data.result;
	});
	$scope.dialog = function(id){
		 $('#'+id).dialog({
		        autoOpen:false
		    });
		 $("#"+id).dialog('open');
	}
});