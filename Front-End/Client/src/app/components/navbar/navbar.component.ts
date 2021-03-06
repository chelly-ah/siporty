import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';

import { AuthenticationService } from '../../services';
import { User } from '../../models';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements AfterViewInit {
    currentUser: User;

    constructor(
        private router: Router,
        private authenticationService: AuthenticationService
    ) {
        this.loadScript('../assets/js/main.js');
        this.authenticationService.currentUser.subscribe(x => this.currentUser = x);
    }

    logout() {

        this.authenticationService.logout();
        this.router.navigate(['/login']);
    }

    ngAfterViewInit() {
        this.loadScript('../assets/js/main.js');
    }

    public loadScript(url) {
        const node = document.createElement('script');
        node.src = url;
        node.type = 'text/javascript';
        document.getElementsByTagName('head')[0].appendChild(node);
    }
}
