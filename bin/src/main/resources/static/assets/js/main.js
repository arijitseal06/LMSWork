/**
* Template Name: Nova - v1.3.0
* Template URL: https://bootstrapmade.com/nova-bootstrap-business-template/
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/
document.addEventListener('DOMContentLoaded', () => {
	"use strict";

	/**
	 * Preloader
	 */
	const preloader = document.querySelector('#preloader');
	if (preloader) {
		window.addEventListener('load', () => {
			preloader.remove();
		});
	}

	/**
	 * Sticky header on scroll
	 */
	const selectHeader = document.querySelector('#header');
	if (selectHeader) {
		document.addEventListener('scroll', () => {
			window.scrollY > 100 ? selectHeader.classList.add('sticked') : selectHeader.classList.remove('sticked');
		});
	}

	/**
	 * Mobile nav toggle
	 */
	const mobileNavShow = document.querySelector('.mobile-nav-show');
	const mobileNavHide = document.querySelector('.mobile-nav-hide');

	document.querySelectorAll('.mobile-nav-toggle').forEach(el => {
		el.addEventListener('click', function(event) {
			event.preventDefault();
			mobileNavToogle();
		})
	});

	function mobileNavToogle() {
		document.querySelector('body').classList.toggle('mobile-nav-active');
		mobileNavShow.classList.toggle('d-none');
		mobileNavHide.classList.toggle('d-none');
	}

	/**
	 * Toggle mobile nav dropdowns
	 */
	const navDropdowns = document.querySelectorAll('.navbar .dropdown > a');

	navDropdowns.forEach(el => {
		el.addEventListener('click', function(event) {
			if (document.querySelector('.mobile-nav-active')) {
				event.preventDefault();
				this.classList.toggle('active');
				this.nextElementSibling.classList.toggle('dropdown-active');

				let dropDownIndicator = this.querySelector('.dropdown-indicator');
				dropDownIndicator.classList.toggle('bi-chevron-up');
				dropDownIndicator.classList.toggle('bi-chevron-down');
			}
		})
	});

	/**
	 * Scroll top button
	 */
	const scrollTop = document.querySelector('.scroll-top');
	if (scrollTop) {
		const togglescrollTop = function() {
			window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
		}
		window.addEventListener('load', togglescrollTop);
		document.addEventListener('scroll', togglescrollTop);
		scrollTop.addEventListener('click', window.scrollTo({
			top: 0,
			behavior: 'smooth'
		}));
	}

	/**
	 * Initiate glightbox
	 */
	const glightbox = GLightbox({
		selector: '.glightbox'
	});

	/**
	 * Init swiper slider with 1 slide at once in desktop view
	 */
	new Swiper('.slides-1', {
		speed: 600,
		loop: true,
		autoplay: {
			delay: 5000,
			disableOnInteraction: false
		},
		slidesPerView: 'auto',
		pagination: {
			el: '.swiper-pagination',
			type: 'bullets',
			clickable: true
		},
		navigation: {
			nextEl: '.swiper-button-next',
			prevEl: '.swiper-button-prev',
		}
	});

	/**
	 * Init swiper slider with 3 slides at once in desktop view
	 */
	new Swiper('.slides-3', {
		speed: 600,
		loop: true,
		autoplay: {
			delay: 5000,
			disableOnInteraction: false
		},
		slidesPerView: 'auto',
		pagination: {
			el: '.swiper-pagination',
			type: 'bullets',
			clickable: true
		},
		navigation: {
			nextEl: '.swiper-button-next',
			prevEl: '.swiper-button-prev',
		},
		breakpoints: {
			320: {
				slidesPerView: 1,
				spaceBetween: 40
			},

			1200: {
				slidesPerView: 3,
			}
		}
	});

	/**
	 * Porfolio isotope and filter
	 */
	let portfolionIsotope = document.querySelector('.portfolio-isotope');

	if (portfolionIsotope) {

		let portfolioFilter = portfolionIsotope.getAttribute('data-portfolio-filter') ? portfolionIsotope.getAttribute('data-portfolio-filter') : '*';
		let portfolioLayout = portfolionIsotope.getAttribute('data-portfolio-layout') ? portfolionIsotope.getAttribute('data-portfolio-layout') : 'masonry';
		let portfolioSort = portfolionIsotope.getAttribute('data-portfolio-sort') ? portfolionIsotope.getAttribute('data-portfolio-sort') : 'original-order';

		window.addEventListener('load', () => {
			let portfolioIsotope = new Isotope(document.querySelector('.portfolio-container'), {
				itemSelector: '.portfolio-item',
				layoutMode: portfolioLayout,
				filter: portfolioFilter,
				sortBy: portfolioSort
			});

			let menuFilters = document.querySelectorAll('.portfolio-isotope .portfolio-flters li');
			menuFilters.forEach(function(el) {
				el.addEventListener('click', function() {
					document.querySelector('.portfolio-isotope .portfolio-flters .filter-active').classList.remove('filter-active');
					this.classList.add('filter-active');
					portfolioIsotope.arrange({
						filter: this.getAttribute('data-filter')
					});
					if (typeof aos_init === 'function') {
						aos_init();
					}
				}, false);
			});

		});

	}

	/**
	 * Animation on scroll function and init
	 */
	function aos_init() {
		AOS.init({
			duration: 800,
			easing: 'slide',
			once: true,
			mirror: false
		});
	}
	window.addEventListener('load', () => {
		aos_init();
	});

});

function getArr(books, choice) {
	if (choice == "title") {
		var arr = books.map(function(el) {
			return el.title;
		});
		console.log(arr);
		return arr;
	}
	else if (choice == "author") {
		var arr = books.map(function(el) {
			return el.author.authorName;
		});
		console.log(arr);
		return arr;
	}
	else {
		var arr = books.map(function(el) {
			return el.genre.genreName;
		});
		console.log(arr);
		return arr;
	}

};

function removeDuplicates(arr) {
	return arr.filter((item,
		index) => arr.indexOf(item) === index);
}

function autoComplete(books) {
	console.log(books);
	var select = document.getElementById("autoSearch");
	var selectedValue = select.value;
	var arr = getArr(books, selectedValue);
	arr = removeDuplicates(arr);
	autocomplete(arr, document.getElementById("myInput"));
	//	console.log(books);

}
function autocomplete(arr, inp = document.getElementById("myInput")) {
	console.log(arr);
	var currentFocus;
	inp.addEventListener("input", function(e) {
		var a, b, i, val = this.value;
		closeAllLists();
		if (!val) { return false; }
		currentFocus = -1;
		a = document.createElement("DIV");
		a.setAttribute("id", this.id + "autocomplete-list");
		a.setAttribute("class", "autocomplete-items");
		this.parentNode.appendChild(a);
		for (i = 0; i < arr.length; i++) {
			if (arr[i].substr(0, val.length).toUpperCase() == val.toUpperCase()) {
				b = document.createElement("DIV");
				b.innerHTML = "<strong>" + arr[i].substr(0, val.length) + "</strong>";
				b.innerHTML += arr[i].substr(val.length);
				b.innerHTML += "<input type='hidden' value='" + arr[i] + "'>";
				b.addEventListener("click", function(e) {
					inp.value = this.getElementsByTagName("input")[0].value;
					closeAllLists();
				});
				a.appendChild(b);
			}
		}
	});
	inp.addEventListener("keydown", function(e) {
		var x = document.getElementById(this.id + "autocomplete-list");
		if (x) x = x.getElementsByTagName("div");
		if (e.keyCode == 40) {
			currentFocus++;
			addActive(x);
		} else if (e.keyCode == 38) { //up
			currentFocus--;
			addActive(x);
		} else if (e.keyCode == 13) {
			e.preventDefault();
			if (currentFocus > -1) {
				if (x) x[currentFocus].click();
			}
		}
	});
	function addActive(x) {
		if (!x) return false;
		removeActive(x);
		if (currentFocus >= x.length) currentFocus = 0;
		if (currentFocus < 0) currentFocus = (x.length - 1);
		x[currentFocus].classList.add("autocomplete-active");
	}
	function removeActive(x) {
		for (var i = 0; i < x.length; i++) {
			x[i].classList.remove("autocomplete-active");
		}
	}
	function closeAllLists(elmnt) {
		var x = document.getElementsByClassName("autocomplete-items");
		for (var i = 0; i < x.length; i++) {
			if (elmnt != x[i] && elmnt != inp) {
				x[i].parentNode.removeChild(x[i]);
			}
		}
	}
	document.addEventListener("click", function(e) {
		closeAllLists(e.target);
	});
}

function searchBasedOnUser(issues) {
	var arr = issues.map(function(el) {
		return el.user.username;
	});
	arr = removeDuplicates(arr);
	autocomplete(arr);
}

function searchBasedOnTitle(issues) {
	var arr = issues.map(function(el) {
		return el.book.title;
	});
	arr = removeDuplicates(arr);
	autocomplete(arr);
}

function bootstrapAlert(msg) {
	$.bootstrapGrowl(`<div id="myDiv">${msg}</div>
        `, {
		ele: "body",
		type: "success",
		offset: { from: "top", amount: 100 },
		delay: 7000,
		allow_dismiss: true,
	});
}