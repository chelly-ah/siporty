import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from 'src/app/services/authentification.service';
import { User } from 'src/app/models';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {
  username;
  constructor(private router: Router,
    private authenticationService: AuthenticationService) {
      this.authenticationService.currentUser.subscribe(x => {
        if (x) {
          this.username = x['user'].username
        }
      });
  }

  ngOnInit() {
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }
}
