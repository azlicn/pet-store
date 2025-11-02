import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DesignPatternDocComponent } from './design-pattern-doc.component';

describe('DesignPatternDocComponent', () => {
  let component: DesignPatternDocComponent;
  let fixture: ComponentFixture<DesignPatternDocComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DesignPatternDocComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DesignPatternDocComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
