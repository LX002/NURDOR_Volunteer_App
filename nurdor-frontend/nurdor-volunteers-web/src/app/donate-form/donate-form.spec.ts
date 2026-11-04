import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DonateForm } from './donate-form';

describe('DonateForm', () => {
  let component: DonateForm;
  let fixture: ComponentFixture<DonateForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DonateForm],
    }).compileComponents();

    fixture = TestBed.createComponent(DonateForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
