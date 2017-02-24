var app = angular.module('JGApp', ['ngDialog']);

app.controller('JGCtrl', function ($scope, ngDialog) {
    $scope.clickToOpen  = function () {
      ngDialog.open({
          template: '<iframe src="https://jabberguest.cisco.com/call/mjamelle@cisco.com?widget=true" width="600" height="400"></iframe>',
          plain: true,
          width: 630,
          height: 430
      });
    };
});
