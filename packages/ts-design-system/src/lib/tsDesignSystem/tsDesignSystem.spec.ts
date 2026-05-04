import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TsDesignSystem } from './tsDesignSystem';

describe('TsDesignSystem', () => {
  let component: TsDesignSystem;
  let fixture: ComponentFixture<TsDesignSystem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TsDesignSystem],
    }).compileComponents();

    fixture = TestBed.createComponent(TsDesignSystem);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
