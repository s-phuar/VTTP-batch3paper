import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RetrieveTag } from '../retrievetag.service';
import { Observable } from 'rxjs';
import { News } from '../model';

@Component({
  selector: 'app-details',
  standalone: false,
  templateUrl: './details.component.html',
  styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit{

  private activatedRotue = inject(ActivatedRoute)
  private retrieveSvc = inject(RetrieveTag)

  tag !: string 
  minutes !: number 
  news$ !: Observable<News[]>
  

  ngOnInit(): void {
    this.activatedRotue.params.subscribe(
      parameters => {
        this.tag = parameters['tag']!
        this.minutes = +parameters['minutes']! //convert value to a number and non-null assertion
        console.info('tag: ', this.tag)
        console.info('minutes: ', this.minutes)
      }
    )

    //make call to backend
    // 2. Spring query param
    this.retrieveSvc.retrieveNews(this.tag, this.minutes).subscribe({
      next: () => console.info('retrieve news method triggered'),
      error: err => console.error('Error:', err)
    });


    this.news$ = this.retrieveSvc.emitter2.asObservable()
  }




}
