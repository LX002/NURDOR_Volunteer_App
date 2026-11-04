import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StartedEventPage } from './started-event-page';

describe('StartedEventPage', () => {
  let component: StartedEventPage;
  let fixture: ComponentFixture<StartedEventPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StartedEventPage],
    }).compileComponents();

    fixture = TestBed.createComponent(StartedEventPage);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
