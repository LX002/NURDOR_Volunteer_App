import { TestBed } from '@angular/core/testing';

import { EventsLogService } from './events-log-service';

describe('EventsLogService', () => {
  let service: EventsLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EventsLogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
