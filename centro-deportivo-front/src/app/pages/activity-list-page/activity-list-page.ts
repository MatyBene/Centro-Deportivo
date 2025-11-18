import { Component, OnInit } from '@angular/core';
import { ActivityService } from '../../services/activity-service';
import SportActivitySummary from '../../models/SportActivitySummary';
import { ActivitySummaryItem } from '../../components/activity-summary-item/activity-summary-item';
import { Pagination } from '../../components/pagination/pagination';
import { SearchBox } from '../../components/search-box/search-box';
import { TimeRangeSearchBox } from "../../components/time-range-search-box/time-range-search-box";

@Component({
  selector: 'app-activity-list-page',
  imports: [ActivitySummaryItem, Pagination, SearchBox, TimeRangeSearchBox],
  templateUrl: './activity-list-page.html',
  styleUrl: './activity-list-page.css'
})
export class ActivityListPage implements OnInit{
  activities: SportActivitySummary[] = [];
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages!: number;
  isLoading: boolean = false;
  currentSearchTerm: string = '';
  currentTimeRange!: {startTime: string, endTime: string};
  showFilters: boolean = false;

  constructor(private activityService: ActivityService){}

  ngOnInit(): void {
    this.loadActivities();
  }

  loadActivities(){
    this.isLoading = true;
    const startTime = Date.now();
    const minLoadingTime = 300;

    let request;

    if (this.currentTimeRange) {
      request = this.activityService.getByTimeRange(
        this.currentTimeRange.startTime, 
        this.currentTimeRange.endTime, 
        this.currentPage, 
        this.pageSize
      );
    } else if (this.currentSearchTerm.trim()) {
      request = this.activityService.getByName(
        this.currentSearchTerm, 
        this.currentPage, 
        this.pageSize
      );
    } else {
      request = this.activityService.getActivities(this.currentPage, this.pageSize);
    }

    request.subscribe({
      next: (data) => {
        const elapsedTime = Date.now() - startTime;
        const remainingTime = Math.max(0, minLoadingTime - elapsedTime);
        
        setTimeout(() => {
          this.activities = data.content;
          this.totalPages = data.totalPages;
          this.isLoading = false;
        }, remainingTime);
      },
      error: (e) => {
        console.log('Error: ', e);
        const elapsedTime = Date.now() - startTime;
        const remainingTime = Math.max(0, minLoadingTime - elapsedTime);
        
        setTimeout(() => {
          this.isLoading = false;
        }, remainingTime);
      }
    })
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadActivities();
  }

  nextPage() {
    if(this.currentPage < this.totalPages - 1){
      this.currentPage++;
      this.loadActivities();
    }
  }

  previousPage() {
    if(this.currentPage > 0){
      this.currentPage--;
      this.loadActivities();
    }
  }

  get hasNextPage(): boolean {
    return this.currentPage < this.totalPages - 1;
  }

  get hasPreviousPage(): boolean {
    return this.currentPage > 0;
  }

  onSearch(searchTerm: string) {
    this.currentSearchTerm = searchTerm;
    this.currentPage = 0;
    this.loadActivities();
  }

  onTimeRangeSearch(timeRange: {startTime: string, endTime: string}) {
    this.currentTimeRange = timeRange;
    this.currentSearchTerm = '';
    this.currentPage = 0;
    this.loadActivities();
  }

  toggleFilters() {
    this.showFilters = !this.showFilters;
  }
}
