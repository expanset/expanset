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
	    },
	    concat: {
	    	js: {
	    		options: {
	    			sourceMap: true,
	    			sourceMapStyle: 'embed'
	    		},	    		
	    		src: [
	    		      'assets/js/jquery.js',
	    		      'assets/js/bootstrap.js',
	    		      'assets/js/chart.js',
	    		      'assets/js/site.js',
	    		      '!assets/js/*.min.js'],
	    		dest: 'assets/js/scripts.js',
	    	},	    	
	    	css: {
	    		options: {
	    			sourceMap: true,
	    			sourceMapStyle: 'embed'
	    		},	    		
	    		src: [
	    		      'assets/css/bootstrap.css',
	    		      '!assets/css/styles.css', 
	    		      '!assets/css/*.min.css'],
	    		dest: 'assets/css/styles.css',
	    	}	    	
	    },
	    uglify: {
	    	options: {
	    		sourceMap: true,
	    		sourceMapIncludeSources: true
	    	},	 
	    	js: {
	    		src: 'assets/js/scripts.js',
	    		dest: 'assets/js/scripts.min.js',
	    	}    	
	    }, 	    
	    cssmin: {
	    	options: {
	    		rebase: true,
	    		sourceMap: true,
	    		sourceMapInlineSources: true, 
	    		target: 'assets/css'
	    	},	    	
	    	css: {
	    		files: [{
	    			expand: true,
	    			cwd: 'assets/css',
	    			src: ['styles.css', '!*.min.css'],
	    			dest: 'assets/css',
	    			ext: '.min.css'
	    		}]
	    	}
	    }		    
	});	
	
	grunt.loadNpmTasks('grunt-bowercopy');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-cssmin');
		
	grunt.registerTask('compile', ['bowercopy']);
	grunt.registerTask('package', ['concat', 'uglify', 'cssmin']);
	grunt.registerTask('all', ['compile', 'package']); 	

	grunt.registerTask('default', [ 'all' ]);	
}