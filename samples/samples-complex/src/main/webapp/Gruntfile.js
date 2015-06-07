'use strict';

module.exports = function(grunt) {
	grunt.initConfig({
		bowercopy: {
			options: {
	            clean: false
	        },
	        libs: {
	            options: {
	                destPrefix: 'assets/js'
	            },
	            files: {
	                'jquery.js': 'jquery/dist/jquery.js',
	                'bootstrap.js': 'bootstrap/dist/js/bootstrap.js',                
	                'chart.js': 'Chart.js/Chart.js'                
	            }
	        },
	        css: {
	            options: {
	                destPrefix: 'assets/css'
	            },
	            files: {
	                'bootstrap.css': 'bootstrap/dist/css/bootstrap.css',
	                'bootstrap.css.map': 'bootstrap/dist/css/bootstrap.css.map'	                
	            }
	        },
	        boostrapFonts: {
	            options: {
	                destPrefix: 'assets'
	            },
	            files: {
	                'fonts': 'bootstrap/dist/fonts'	                
	            }
	        }	        
	    }
	});	
	
	grunt.loadNpmTasks('grunt-bowercopy');
	
	grunt.registerTask('main', [ 'bowercopy' ]);

	grunt.registerTask('default', [ 'main' ]);	
}