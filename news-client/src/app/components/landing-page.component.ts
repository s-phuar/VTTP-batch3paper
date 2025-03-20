import { Component, inject, OnInit } from '@angular/core';
import { RetrieveTag } from '../retrievetag.service';
import { TagCount } from '../model';
import { Observable, Subscription } from 'rxjs';

@Component({
  selector: 'app-landing-page',
  standalone: false,
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.css'
})
export class LandingPageComponent  implements OnInit{

  private retrieveSvc = inject(RetrieveTag)
  retrieve$ !: Observable<TagCount[]>
  duration :number  =5

  ngOnInit(): void {
    this.retrieve$ = this.retrieveSvc.emitter.asObservable()
    this.duration = this.retrieveSvc.getDuration()
    // 2. Spring query param
    this.retrieveSvc.retrieve(this.duration).subscribe({
      next: () => console.info('retrieve method triggered'), //expecting retrieval from 
      error: err => console.error('Error:', err)
    });

  }

  timeChange($event: any){
    const newValue = +$event.target.value //ensure its a number
    console.info('newValue: ', newValue)
    this.duration = newValue
    this.retrieveSvc.setDuration(newValue)


    // 2. Spring query param
    this.retrieveSvc.retrieve(newValue).subscribe({
      next: () => console.info('retrieve method triggered'), //expecting retrieval from 
      error: err => console.error('Error:', err)
    });
  }
  

}
